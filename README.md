<h1 align="center">
  <a href="https://github.com/brandonfl/discord-virustotal-bot"><img src="https://github.com/brandonfl/discord-virustotal-bot/blob/assets/virustotal.png?raw=true" width="100"/></a>
  <br>
  <a href="https://github.com/brandonfl/discord-virustotal-bot">Discord VirusTotal bot</a>
  <br>
</h1>

<h4 align="center"> Discord bot using VirusTotal API to check if the content of messages sent by discord users is safe

<p align="center">
  <a href="https://github.com/brandonfl/discord-virustotal-bot/releases"><img src="https://img.shields.io/github/v/release/brandonfl/discord-virustotal-bot" alt="release"></a>
  <a href="https://github.com/brandonfl/discord-virustotal-bot/actions?query=workflow%3Abuild-docker-and-publish"><img src="https://github.com/brandonfl/discord-virustotal-bot/workflows/build-docker-and-publish/badge.svg" alt="github-docker"></a>
  <a href="https://github.com/brandonfl/discord-virustotal-bot/actions?query=workflow%3Asonar-gate"><img src="https://github.com/brandonfl/discord-virustotal-bot/workflows/sonar-gate/badge.svg" alt="github-sonar"></a>
  <a href="https://sonarcloud.io/project/overview?id=brandonfl_discord-virustotal-bot"><img src="https://sonarcloud.io/api/project_badges/measure?project=brandonfl_discord-virustotal-bot&metric=alert_status" alt="sonar-gate"></a>
  <a href="https://github.com/brandonfl/discord-virustotal-bot/blob/master/LICENSE"><img src="https://img.shields.io/github/license/brandonfl/discord-virustotal-bot" alt="licence"></a>
</p>

<p align="center">
  <a href="#how-to-use">How to use</a> •
  <a href="#variables">Variables</a> •
  <a href="#licence">Licence</a> 
</p>

## How to use

#### Use with docker run
Command 
`docker run IMAGE -e BOT_TOKEN=TOKEN ...` 

with `-e` the <a href="#variables">variables</a>

#### Use with java
1. Compile `mvn clean package`
2. Run `java -jar target/bot.war` with <a href="#variables">variables</a>

#### Use with tomcat
1. Compile `mvn clean package` and get the war file in `target`folder
2. Config the config file of your bot `CATALINA-HOME/conf/Catalina/localhost/bot.xml` with <a href="#variables">variables</a>
3. Deploy the war `CATALINA-HOME/webapps/bot.war`

## Variables

| Key | Description | Default |
|--|--|--|
| LOG_FILE | Location of log file | ./log/bot.log |
| BOT_TOKEN | Token of the Discord bot | None - **required** |
| VIRUS_TOTAL_TOKEN | Token of the VirusTotal API | None - **required** |
| VIRUS_TOTAL_MAX_POSITIVE_SCORE_FOR_BLACKLIST | Max number of positive score to backlist the url. More the positive score is high, more the url represents a risk. | 3 |
| DB_USERNAME | The username used for your database | bot |
| DB_PASSWORD | The password used for your database | bot |
| DB_FILE-PATH | Where your database files are stored | ./data/discordvirustotal |
| DB_NAME | The name of the table | bot |

## Licence

Project under [MIT](https://github.com/brandonfl/discord-virustotal-bot/blob/master/LICENSE) licence
