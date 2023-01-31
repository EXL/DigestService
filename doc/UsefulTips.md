Useful Tips
===========

## Find all files without line ending at the end

```shell script
find src/ doc/ util/ -type f -print0 | xargs -0 -L1 bash -c 'test "$(tail -c 1 "$0")" && echo "No new line at end of $0"'
```

This command should be run at the root of the project.

## Find trailing spaces on files

```shell script
grep -r '[[:blank:]]$' src/ doc/ util/
```
This command should be run at the root of the project.

## Find stored a static file in the Nginx cache

```shell script
grep -lr 'login.css' /var/nginx/cache/*
```

This command should be run on the server side with root user privileges.

## Add license header to files by name mask

```shell script
find . -name \*.java -exec sh -c "mv '{}' tmp && cp util/template/license_header_common '{}' && cat tmp >> '{}' && rm tmp" \;
find . -name \*.py -exec sh -c "mv '{}' tmp && cp util/template/license_header_other '{}' && cat tmp >> '{}' && rm tmp" \;
```

* [license_header_common](../util/template/license_header_common) template applies to the C, C++, Java source files.
* [license_header_other](../util/template/license_header_other) template applies to the Python and other source files.

These commands should be run at the root of the project.

## Show all changed files from date and tag

```shell script
git log --since="01-Jan-2021" --name-only --pretty=format: | sort | uniq
git log v1.0.1..HEAD --name-only --pretty=format: | sort | uniq
```

## Add additional field to the Bot Setup table

```sql
ALTER TABLE bot_setup ADD COLUMN send_motofan_birthdays BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE bot_setup ADD COLUMN use_button_captcha BOOLEAN NOT NULL DEFAULT FALSE;

SELECT * FROM bot_setup;
```

## Execute only one test class via Gradle

```shell script
./gradlew test -i --tests ru.exlmoto.digest.bot.handler.BotHandlerMockTest
```
