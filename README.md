# Зачем он нужен
По умолчанию Telegram даёт возможность любому администратору удалять участников из канала (ЧС), даже если у него нет прав. Это открывает возможность атаки на ваш канал.
# Что делает бот
После установки, вы можете выдать ранее созданную роль через бота. Обязательно выдавать роли через него, т.к удалить админа из канала может только тот, кто его назначил. Если редактор решит без вашего ведома удалить одного из участников канала, его админ-права немедленно будут анулированы, а участник разбанен.
# Установка
1. Поместите .jar файл в отдельную папку, она будет выделена под бота
2. Создайте start.sh или start.bat (примеры ниже) для удобного запуска
3. Настройте config.json, создайте роли. Если конфига нет — запустите бота, он его создаст. Пример конфига ниже
## config.json
В категории "roles" можно добавлять свои роли с уникальными правами. Выдавать вы их будете по ID.
```json
{
	"token": "yourBotTokenHere",
	"username": "username_bot",
	"debug": true,
	"roles": [
		{
			"id": "editor",
			"name": "Редактор",
			"canChangeInformation": false,
			"canPostMessages": true,
			"canEditMessages": true,
			"canDeleteMessages": false,
			"canInviteUsers": true,
			"canRestrictMembers": false,
			"canPinMessages": true,
			"canPromoteMembers": false
		},
		{
			"id": "admin",
			"name": "Админ",
			"bypass": true,
			"canChangeInformation": false,
			"canPostMessages": true,
			"canEditMessages": true,
			"canDeleteMessages": false,
			"canInviteUsers": true,
			"canRestrictMembers": true,
			"canPinMessages": true,
			"canPromoteMembers": true
		}
	]
}
```
## Запускатор start.sh
```shell
#!/bin/sh

APP_JAR="TelegramDefenderBot-1.0.jar"
HEAP_SIZE="128M"          # Оперативная память
JAVA_BIN="$(command -v java || true)"
LOG_DIR="logs"
OUT_LOG="$LOG_DIR/out.log"
ERR_LOG="$LOG_DIR/err.log"
MAX_RESTARTS=5            # Макс. количество рестартов в короткий период
RESTART_WINDOW=60         
RESTART_COUNT=0
FIRST_RESTART_TS=0

mkdir -p "$LOG_DIR"

if [ -z "$JAVA_BIN" ]; then
  echo "Java не найдена в PATH" >&2
  exit 1
fi

if [ ! -f "$APP_JAR" ]; then
  echo "JAR-файл '$APP_JAR' не найден" >&2
  exit 1
fi

while true
do
  now_ts=$(date +%s)

  # сбрасываем счётчик рестартов, если окно прошло
  if [ "$FIRST_RESTART_TS" -ne 0 ] && [ $((now_ts - FIRST_RESTART_TS)) -gt "$RESTART_WINDOW" ]; then
    RESTART_COUNT=0
    FIRST_RESTART_TS=0
  fi

  if [ "$RESTART_COUNT" -ge "$MAX_RESTARTS" ]; then
    echo "$(date '+%Y-%m-%d %H:%M:%S') Превышено число рестартов ($RESTART_COUNT) в пределах $RESTART_WINDOW сек. Останов." | tee -a "$ERR_LOG"
    exit 2
  fi

  if [ "$RESTART_COUNT" -eq 0 ]; then
    FIRST_RESTART_TS=$now_ts
  fi

  echo "$(date '+%Y-%m-%d %H:%M:%S') Запуск приложения..." | tee -a "$OUT_LOG"
  "$JAVA_BIN" -Xms"$HEAP_SIZE" -Xmx"$HEAP_SIZE" -XX:+UseG1GC -Dfile.encoding=UTF-8 -jar "$APP_JAR" >>"$OUT_LOG" 2>>"$ERR_LOG"
  EXIT_CODE=$?

  echo "$(date '+%Y-%m-%d %H:%M:%S') Процесс завершился с кодом $EXIT_CODE" | tee -a "$OUT_LOG"

  RESTART_COUNT=$((RESTART_COUNT + 1))
  sleep 3
done
```
## Команды
* /setrole [айди_юзера] [айди_канала] [айди_роли_из_конфига] — Установить нужную роль
* /roles — Посмотреть список всех ролей
* /ram — Проверить оперативную память
### Пример
/setrole 6360364547 -1001605735977 admin
### Где взять айди
В веб-версии телеграм или через других ботов. При открытии чатов и каналов его можно найти здесь: https://web.telegram.org/a/#айди
# Обратная связь
Я работаю с API Telegram не так много, в боте могут быть ошибки или другие косяки.
Пишите, не стесняйтесь: https://t.me/deelter
