#!/usr/bin/env python3
########################################################################################################################
## Install PIP packages:                                                                                              ##
##      # pip3 install psycopg2                                                                                       ##
##      # pip3 install telethon                                                                                       ##
##      # pip3 install mysql-connector-python                                                                         ##
########################################################################################################################

import mysql.connector
import psycopg2
import time
import subprocess
from config_db import *
from telethon import TelegramClient, events, sync


def Debug(aString):
    print(aString)
    print(aString, file = log)

def FormatDigestMessage(aDigest):
    digest = str(aDigest).replace("'", '"')
    assert not "'" in digest
    assert not "`" in digest
    return subprocess.getoutput("java -jar FormatDigestMessage.jar '{}'".format(digest.strip()))

def ActivateUsersInDigest(aUsername):
    return '<a href="https://t.me"' + aUsername + '" title="@' + aUsername + '" target="_blank">' + aUsername + '</a>'

def DoingImport():
    # Open Telegram Client
    telegram_client = TelegramClient('get_ids', TG_API_ID, TG_API_HASH)
    telegram_client.start()

    # Connect to databases:
    db_first_connection = mysql.connector.connect(user=DB_USER, password=DB_PASS, database=DB_DATA, host=DB_HOST)
    db_second_connection = psycopg2.connect(user=DB_USER, password=DB_PASS, database=DB_DATA, host=DB_HOST)

    # Get cursors:
    cursor_first = db_first_connection.cursor()
    cursor_second = db_second_connection.cursor()

    # Set queries:
    query_first_digest = ('SELECT * FROM digests')
    query_first_digest_users = ('SELECT * FROM digests_users')
    query_second_digest = ('TRUNCATE digestbot_entries')
    query_second_digest_users = ('TRUNCATE digestbot_users')

    # Prepare to commit users:
    cursor_first.execute(query_first_digest_users)
    cursor_second.execute(query_second_digest_users)
    db_second_connection.commit()
    Debug('=> Commit digest users.')

    # Commit users to database:
    users_without_id = 0
    for (username, avatar) in cursor_first:
        try:
            user_id = telegram_client.get_entity(username).id
        except:
            user_id = users_without_id + 2
            users_without_id += 1
        time.sleep(2)
        Debug('Commit {}: {}'.format(username, user_id))
        cursor_second.execute('INSERT INTO digestbot_users (id, avatar_link, username, username_ok, username_html) '
                              'VALUES (%s, %s, %s, %s, %s)',
                (user_id, avatar, username, True, ActivateUsersInDigest(username)))
    db_second_connection.commit()

    # Prepare to commit digests:
    cursor_first.execute(query_first_digest)
    cursor_second.execute(query_second_digest)
    db_second_connection.commit()
    Debug('=> Commit digests.')

    # Commit digests:
    digest_id = 1
    for (date, username, msg) in cursor_first:
        cursor_second.execute('SELECT id FROM digestbot_users WHERE username LIKE %s', (username,))
        username_id = cursor_second.fetchone()[0]
        cursor_second.execute('INSERT INTO digestbot_entries (id, author, chat, date, digest, digest_html, message_id) '
                              'VALUES (%s, %s, %s, %s, %s, %s, %s)',
                (digest_id, username_id, MOTOFAN_CHAT, date, msg, FormatDigestMessage(msg), None))
        Debug('\nCommit digest {} date {} from {}:\nOrig: {}\nHtml: {}\n'
            .format(digest_id, date, username, msg, FormatDigestMessage(msg)))
        db_second_connection.commit()
        digest_id += 1

    # Close all connections:
    cursor_first.close()
    cursor_second.close()
    db_first_connection.close()
    db_second_connection.close()

if __name__ == '__main__':
    global log
    log = open('import.log', 'w')
    DoingImport()
    log.close()
