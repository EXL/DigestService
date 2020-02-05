#!/usr/bin/env python3

import os
import mysql.connector
import psycopg2
import time
import subprocess

from telethon import TelegramClient, events, sync


class Logger:
	def __init__(self, name):
		self.log = open('import.log', 'w')
		self.name = name

	def __del__(self):
		self.log.close()

	def debug(self, output):
		print(self.name + ': ' + output)
		print(self.name + ': ' + output, file=self.log)

	def error(self, output):
		self.debug(output)
		exit(1)


class Config:
	def __init__(self):
		log = Logger(self.__class__.__name__)
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
		self.client = TelegramClient('get_ids', cfg['TG_API_ID'], cfg['TG_API_ID'])
		self.client.start()


class Importer:
	def __init__(self, cfg, tg):
		self.tg = tg
		self.cfg = cfg
		self.log = Logger(self.__class__.__name__)
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


if __name__ == '__main__':
	config = Config().cfg
	telegram = Telegram(config)
	importer = Importer(config, telegram)
