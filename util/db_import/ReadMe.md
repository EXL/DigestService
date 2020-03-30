db_import
=========

Utility script for import old digest database from [DigestBot](https://github.com/EXL/DigestBot) project.

## Dependencies

```sh
$ pip3 install mysql-connector-python
$ pip3 install psycopg2
$ pip3 install telethon
```

## Usage

```sh
$ cd db_import/
$ TG_API_ID=<id> TG_API_HASH=<hash> MF_CHAT_ID=<id> \
    DB_USER_1=<user> DB_PASS_1=<password> DB_NAME_1=<database> DB_HOST_1=<host> \
    DB_USER_2=<user> DB_PASS_2=<password> DB_NAME_2=<database> DB_HOST_2=<host> \
    ./db_import.py
```

Do not forget to create tables before running the import script. After execution, user avatars should be updated.
