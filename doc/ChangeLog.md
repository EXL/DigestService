## Upcoming Release:

- Added anti-spam system and CAPTCHA question.

- Added quotes from RBC and AliExpress to rates report.

- Added Bitcoin currency to the rates report (suggestion by @mbv06).

- Added sending daily exchange rates report.

- Moved from Integer to Long type for user id fields (see [this issue](https://github.com/pengrad/java-telegram-bot-api/issues/230)).

- Fixed birthdays of MotoFan.Ru forum users report.

- Fixed and increased exchange rates precision (suggestion by @baaaaat).

- Fixed BYN exchange rates (reported by @baaaaat).

- Fixed COVID-19 pre-rendered report column length.

- Fixed non-string fields in JSON generators.

- Fixed HTML escaping in MotoFan.Ru posts.

- Updated libraries and frameworks:
    - Java 1.8.0_275 => 1.8.0_312
    - Gradle 6.7.1 => 7.4.1
    - Spring Boot 2.4.1 => 2.6.6
    - Spring Boot Dependency Management 1.0.10.RELEASE => 1.0.11.RELEASE
    - com.github.pengrad:java-telegram-bot-api 5.0.1 => 5.7.0
    - org.yaml:snakeyaml 1.27 => 1.29
    - org.jsoup:jsoup 1.13.1 => 1.14.3
    - org.apache.poi:poi 4.1.2 => 5.2.2
    - org.apache.poi:poi-ooxml 4.1.2 => 5.2.2

## v1.0.1, 27-Dec-2020, Patch Release:

- Added birthdays of MotoFan.Ru forum users to the morning report.

- Added integration with some Game Servers like Quake II.

- Added pre-rendered COVID-19 reports (suggestion by @ZorgeR).

- Added useful for fixing typos digest editing feature.

- Changed determining external IP service to AWS.

- Disabled proxying due RKN unblocking.

- Separated exchange rate API (suggestion by @baaaaat).

- Separated COVID-19 reports (suggestion by @mbv06).

- Fixed same chat digest subscribing.

- Fixed some errors on various external requests.

- Fixed some errors in parsers and generators.

- Fixed some typos in text resources and code.

- Moved to new hosting by Oracle Cloud.

- Updated libraries and frameworks:
    - Java 1.8.0_252 => 1.8.0_275
    - Gradle 6.3 => 6.7.1
    - Spring Boot 2.3.0 => 2.4.1
    - Spring Boot Dependency Management 1.0.9.RELEASE => 1.0.10.RELEASE
    - com.github.pengrad:java-telegram-bot-api 4.8.0 => 5.0.1
    - org.yaml:snakeyaml 1.26 => 1.27
    - org.owasp.encoder:encoder 1.2.2 => 1.2.3

### Technical Notes:

```bash
git log --pretty=oneline --abbrev-commit | wc -l
1023

cloc src/main/
     171 text files.
     171 unique files.
       5 files ignored.

github.com/AlDanial/cloc v 1.82  T=0.51 s (327.1 files/s, 34986.6 lines/s)
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Java                           143           2426           3390          10132
HTML                            19              0             62           1030
CSS                              3              7              9            538
YAML                             1              0              0            160
-------------------------------------------------------------------------------
SUM:                           166           2433           3461          11860
-------------------------------------------------------------------------------
```

## v1.0.0, 25-May-2020, First Public Release:

- Now Digest Service source code is available on GitHub, GitLab, and Bitbucket resources.

    - https://github.com/EXL/DigestService
    - https://gitlab.com/EXL/DigestService
    - https://bitbucket.org/exlmotodev/digestservice

- Added Control Module for Telegram bot administrators.

- Added "Digest Service and Telegram Bot Commands Cheat Sheet" help manual in Russian and English.

- Added COVID-19 statistics report for Ukraine (suggestion by @mbv06).

- Added a difference for each of the currencies in quotes for the **/rate** command (suggestion by @baaaaat).

- Added a real estate reports for CIAN and N1 resources.

### Technical Notes:

```bash
git log --pretty=oneline --abbrev-commit | wc -l
921

cloc src/main/
     166 text files.
     166 unique files.
       5 files ignored.

github.com/AlDanial/cloc v 1.82  T=1.76 s (91.5 files/s, 9596.9 lines/s)
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Java                           138           2303           3252           9530
HTML                            19              0             62           1021
CSS                              3              7              9            538
YAML                             1              0              0            160
-------------------------------------------------------------------------------
SUM:                           161           2310           3323          11249
-------------------------------------------------------------------------------
```
