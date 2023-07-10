if [[ -z "$COUNT" ]]
then
    COUNT=10
fi
if [[ -z "$SEED" ]]
then
    SEED=3
fi

export SERVICE_VERSION=`mvn help:evaluate -Dexpression=project.version -q -DforceStdout`
export COUNT=$COUNT
export SEED=$SEED

echo $SERVICE_VERSION $SEED $COUNT

if ! command -v docker &> /dev/null
then
    echo "docker is not installed"
    exit
fi

if ! command -v mvn &> /dev/null
then
    echo "mvn is not installed"
    exit
fi

mvn clean install
docker build -t loggerator-app:latest . -f DockerFile
docker-compose up
