#!/usr/bin/env python3

import os
import time

import mysql.connector
import psycopg2
import telethon.sync


class Logger:
	def __init__(self):
		self.log = open('import.log', 'w')

	def close(self):
		self.debug('Exiting...')
		self.log.close()

	def debug(self, output):
		print(output)
		print(output, file=self.log)

	def error(self, output):
		self.debug(output)
		self.close()
		exit(1)


class Config:
	def __init__(self, log):
		try:
			self.cfg = dict(
				TG_API_ID=os.environ['TG_API_ID'],
				TG_API_HASH=os.environ['TG_API_HASH'],
				DB_USER_1=os.environ['DB_USER_1'],
				DB_USER_2=os.environ['DB_USER_2'],
				DB_PASS_1=os.environ['DB_PASS_1'],
				DB_PASS_2=os.environ['DB_PASS_2'],
				DB_NAME_1=os.environ['DB_NAME_1'],
				DB_NAME_2=os.environ['DB_NAME_2'],
				DB_HOST_1=os.environ['DB_HOST_1'],
				DB_HOST_2=os.environ['DB_HOST_2'],
				MF_CHAT_ID=os.environ['MF_CHAT_ID']
			)
		except KeyError as ke:
			log.error(
				str.format(
					'Cannot get {} environment variable.\n\n'
					'Usage:\n'
					'\tTG_API_ID=<id> TG_API_HASH=<hash> MF_CHAT_ID=<id> \\\n'
					'\tDB_USER_1=<user> DB_PASS_1=<password> DB_NAME_1=<database> DB_HOST_1=<host> \\\n'
					'\tDB_USER_2=<user> DB_PASS_2=<password> DB_NAME_2=<database> DB_HOST_2=<host> \\\n'
					'\t{}', ke, __file__
				)
			)


class Telegram:
	def __init__(self, cfg):
		self.client = telethon.TelegramClient('get_ids', cfg['TG_API_ID'], cfg['TG_API_HASH'])
		self.client.start()


class Importer:
	def __init__(self, cfg, tg, log):
		self.tg = tg
		self.cfg = cfg
		self.log = log

		self.conn_db_1 = mysql.connector.connect(
			user=cfg['DB_USER_1'], password=cfg['DB_PASS_1'], database=cfg['DB_NAME_1'], host=cfg['DB_HOST_1']
		)
		self.conn_db_2 = psycopg2.connect(
			user=cfg['DB_USER_2'], password=cfg['DB_PASS_2'], database=cfg['DB_NAME_2'], host=cfg['DB_HOST_2']
		)
		if not self.conn_db_1:
			self.log.error(str.format('Cannot connect to the {} database.', cfg['DB_NAME_1']))
		if not self.conn_db_2:
			self.log.error(str.format('Cannot connect to the {} database.', cfg['DB_NAME_2']))

		self.curr_db_1 = self.conn_db_1.cursor()
		self.curr_db_2 = self.conn_db_2.cursor()

	def close(self):
		self.curr_db_2.close()
		self.curr_db_1.close()
		self.conn_db_2.close()
		self.conn_db_1.close()

	def import_digest(self):
		self.log.debug('=> Start commit digests')
		self.curr_db_2.execute('TRUNCATE TABLE bot_digest')
		self.conn_db_2.commit()

		self.curr_db_1.execute('SELECT * FROM digests')
		digest_id = 1
		for (date, username, msg) in self.curr_db_1:
			self.curr_db_2.execute('SELECT id FROM bot_digest_user WHERE username LIKE %s', ('@' + username,))
			user_id = self.curr_db_2.fetchone()[0]
			self.log.debug(str.format('Commit digest {} from {} at timestamp {}.', digest_id, username, date))
			self.curr_db_2.execute(
				'INSERT INTO bot_digest (id, chat, date, message_id, digest, user_id) VALUES (%s, %s, %s, %s, %s, %s)',
				(digest_id, self.cfg['MF_CHAT_ID'], date, None, msg, user_id)
			)
			self.conn_db_2.commit()
			digest_id += 1
		self.log.debug('=> End commit digests')

	def import_users(self):
		self.log.debug('=> Start commit digest users.')
		self.curr_db_2.execute('TRUNCATE TABLE bot_digest_user CASCADE')

		user_without_id = 0
		self.curr_db_1.execute('SELECT * FROM digests_users')
		for (username, avatar) in self.curr_db_1:
			try:
				user_id = self.tg.get_entity(username).id
			except ValueError:
				user_id = user_without_id + 2
				user_without_id += 1
			time.sleep(2)
			self.log.debug(str.format('Commit user {}: {}', username, user_id))
			self.curr_db_2.execute(
				'INSERT INTO bot_digest_user (id, avatar, username) VALUES (%s, %s, %s)',
				(user_id, avatar, '@' + username)
			)
			self.conn_db_2.commit()
		self.log.debug('=> End commit digest users.')


if __name__ == '__main__':
	logger = Logger()
	config = Config(logger).cfg
	client = Telegram(config).client
	importer = Importer(config, client, logger)
	importer.import_users()
	importer.import_digest()
	importer.close()
	logger.close()
