Settings Description
====================

See [application.properties](../src/main/resources/application.properties) file for the application properties.

## Spring Framework Parameters

### Spring Database Options

* *spring.datasource.url* - Set URL connection scheme to database via environment variable.

    Example:

    ```
    DB_CONNECTION=jdbc:postgresql://localhost:5432/digest_test
    spring.datasource.url=${DB_CONNECTION}
    ```

* *spring.datasource.username* - Set database username via environment variable.

    Example:

    ```
    DB_USERNAME=<username>
    spring.datasource.username=${DB_USERNAME}
    ```

* *spring.datasource.password* - Set database password via environment variable.

    Example:

    ```
    DB_PASSWORD=<password>
    spring.datasource.password=${DB_PASSWORD}
    ```

* *spring.jpa.hibernate.ddl-auto* - Set generating database scheme.

    Useful options:

    * `create` - Drop all tables before running the application and create them again.
    * `update` - Update database scheme if there are changes without deleting data.

    Additional information:

    * [Quick Guide on Loading Initial Data with Spring Boot | Controlling Database Creation Using Hibernate](https://www.baeldung.com/spring-boot-data-sql-and-schema-sql#controlling-database-creation-using-hibernate).

* *spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation* - Enable or disable contextual LOB warnings at application start.

    Additional information:

    * [Spring Boot and PostgreSQL](https://dzone.com/articles/spring-boot-and-postgresql).
    * [Disabling contextual LOB creation as createClob() method threw error](https://stackoverflow.com/questions/4588755/disabling-contextual-lob-creation-as-createclob-method-threw-error).

* *spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults* - Enable or disable contextual LOB warnings at application start.

    Additional information:

    * [Disabling contextual LOB creation as createClob() method threw error](https://stackoverflow.com/questions/4588755/disabling-contextual-lob-creation-as-createclob-method-threw-error).

* *spring.jpa.database-platform* - Set database platform engine.

    Example for PostgreSQL 9:

    ```
    spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL9Dialect
    ```

* *spring.jpa.open-in-view* - Enable or disable OSIV in Spring Boot application.

    Additional information:

    * [A Guide to Spring’s Open Session In View](https://www.baeldung.com/spring-open-session-in-view).
    * [The OSIV Anti-Pattern](https://stackoverflow.com/a/48222934).

### Spring Cache Options

* *spring.resources.chain.enabled* - Enable or disable Spring Resource Handling chain.

* *spring.resources.chain.strategy.content.enabled* - Enable or disable the content Version Strategy.

    Note: Use such filenames `name-a0a19ef66f2d3968e0889e8e3f7bcbf5.png` instead of `name.png` for static files.

* *spring.resources.chain.html-application-cache* - Enable or disable HTML5 application cache manifest rewriting.

* *spring.resources.chain.strategy.content.paths* - Set a comma-separated list of patterns to apply to the content Version Strategy.

    Example:

    ```
    spring.resources.chain.strategy.content.paths=/icon/**,/image/**,/style/**
    ```

* *spring.resources.cache.cachecontrol.max-age* - Set maximum time the response should be cached, in seconds if no duration suffix isn't specified.

    Example:

    ```
    spring.resources.cache.cachecontrol.max-age=365d
    ```

## Application Parameters

### Logging Level

* *logging.level.root* - Set global application logging level via environment variable.

    Example:

    ```
    LOGGING=DEBUG
    logging.level.root=${LOGGING:#{INFO}}
    ```

    If there is no environment variable then default `INFO` value will be used.

### General Options

* *general.lang* - Set the default language for the application.

* *general.username-tag* - Set default pattern for inserting username into text.

* *general.date-format* - Set default date and time format.

* *general.url-host-ip* - Set URL for getting public IP address in plain text format.

### Site Options

* *site.address* - Set site host URL address via environment variable.

    Example:

    ```
    HOST=//digest.exlmoto.ru/
    site.address=${HOST}
    ```

* *site.page-posts* - Set digest count per page.

* *site.page-deep* - Set digest pager width for.

* *site.page-posts-admin* - Set digest count per page in the administration module.

* *site.page-deep-admin* - Set digest pager width in the administration module.

* *site.motofan-chat-slug* - Set main chat slug cast.

    Example:

    ```
    site.motofan-chat-slug=@motofan_ru
    ```

* *site.moderators* - Set a comma-separated list of the usernames of the main chat admins (not bot admins).

    Example:

    ```
    site.moderators=yakimka,mbv06
    ```

* *site.proxy-enabled* - Enable or disable proxy for avatars.

    Note: This is used to bypass Telegram resources blocking.

* *site.proxy* - Set URL address path for proxy.

    Example:

    ```
    site.proxy=//digest.exlmoto.ru/proxy/
    ```

    Note: See an example of the proxy server in the [digest.conf](../util/nginx/digest.conf) Nginx config file.

* *site.autolinker-enabled* - Enable or disable an auto-linking via 3rd party library.

    * `true` - Use [autolink-java](https://github.com/robinst/autolink-java) library for an auto-linking.
    * `false` - Use RegExp for an auto-linking.

* *site.obey-protection* - Enable or disable administration module protection via environment variable.

    Example:

    ```
    PROTECT=true
    site.obey-protection=${PROTECT}
    ```

    Note: This is a useful option for the first deploy.

* *site.obey-debug-password* - Set default password for a login when administration module protection disabled.

* *site.obey-debug-role* - Set default role for a login user when administration module protection disabled.

### Telegram Bot Options

* *bot.initialize* - Enable or disable bot initialization.

    Note: This is a useful option for running and debugging the application without a VPN connection when Telegram blocked.

* *bot.token* - Set bot token via environment variable.

    Example:

    ```
    TG_TOKEN=<token>
    bot.token=${TG_TOKEN}
    ```

* *bot.admins* - Set a comma-separated list of the usernames of the bot admins (not main chat admins).

    Example:

    ```
    bot.admins=exlmoto,ZorgeR
    ```

* *bot.max-updates* - Set maximum updates count for handling after cold start.

* *bot.max-send-length* - Set maximum length of bot message.

    Note: The message will be cropped if it goes beyond this limit. Telegram client limit is 4096 characters.

* *bot.disable-notifications* - Enable or disable user notifications in the bot messages.

* *bot.log-updates* - Enable or disable updates logging.

    Note: Logs will be sent to the system log which can be viewed using the following command:

    ```shell script
    journalctl -u digest
    ```

* *bot.show-greetings* - Enable or disable bot greetings globally.

    The following bot responses will be affected in all chats:

    * New Users Events.
    * Left User Event.
    * Change Group Photo Event.

* *bot.silent* - Enable or disable sending all bot messages to all chats.

* *bot.use-stack* - Use stack for detecting and avoid mass buttons clicking.

    Note: If this option disabled the Java Threads will be used for detecting and avoid mass buttons clicking.

* *bot.cooldown* - Set cooldown delay in seconds for some actions e.g. to avoid mass buttons clicking.

* *bot.message-delay* - Set message delay between sending bot posts.

    Note: Just in case to avoid the ban of the bot by the Telegram system.

* *bot.sticker-coffee* - Set Sticker ID for `/coffee` command.

* *bot.url-game* - Set URL to Game Servers image API service.

* *bot.motofan-chat-id* - Set main chat ID via environment variable.

    Example:

    ```
    TG_CHAT=-1001045117849
    bot.motofan-chat-id=${TG_CHAT}
    ```

    Note:

    * `-1001045117849` is MotoFan.Ru chat.
    * `-1001148683293` is Debug chat.

* *bot.motofan-chat-url* - Set main chat direct URL link.

* *bot.telegram-short-url* - Set short Telegram URL link service.

* *bot.max-digest-length* - Set maximum length of digest sent by user.

* *bot.show-page-posts* - Set digest count per page for `/show` command.

* *bot.digest-page-posts* - Set digest count per page for `/digest` command.

* *bot.digest-page-deep* - Set digest pager width for `/show` and `/digest` commands.

* *bot.obsolete-data-delay* - Set digest storage time in seconds.

    Note: This setting used for digests of other chats. Digests in the main chat stored permanently.

    * `45` seconds for debug.
    * `43200` seconds for 12-hours.
    * `86400` seconds for 24-hours.
    * `172800` seconds for 48-hours.
    * `604800` seconds for a week.
    * `648000` seconds for a week + one day.

* *bot.digest-shredder* - Enable or disable the deletion of digests after the expiration of the storage time.

### Rest Template Options

* *rest.timeout-sec* - Set general connection timeouts in seconds.

* *rest.max-body-size* - Set maximum file size for downloading in bytes.

    Example:

    ```
    rest.max-body-size=5242880
    ```

    Note: 5242880 bytes is 5 MiB.

* *rest.simple-http-client* - Enable or disable using Simple HTTP Client instead of OkHttp or Apache.

* *rest.fake-user-agent* - Set fake user agent for Rest Template queries.

    Example for typical Firefox ESR user agent on Fedora Linux distro:

    ```
    rest.fake-user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0
    ```

    Note: This is applies for header request as well.

### Image Downloader Options

* *image.download-file* - Enable or disable images downloading.

    Note: If this option disabled the downloader will return the image URL link back and Telegram will download the file and generate a preview itself.

* *image.use-image-io-read* - Use the standard ImageIO library to verify the downloaded image.

    Note: If this option disabled the MIME type (Media type) will be used to verify the downloaded image.

### MotoFan.Ru Crawler Options

* *motofan.last-post-url* - Set URL for MotoFan.Ru JSON API service.

### Exchange Rate Options

This section contains API links for obtaining exchange rates data.

### COVID Service Options

* *covid.url* - Set the main URL for the COVID API service.

* *covid.date-format* - Set date format for output reports.

* *covid.text.to.image* - Use images instead of text for Telegram bot reports.

### Cron Options

This section contains cron time strings for various services and workers.
