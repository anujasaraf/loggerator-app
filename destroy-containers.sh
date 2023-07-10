export SERVICE_VERSION=ANY
export COUNT=10
export SEED=3

if ! command -v docker &> /dev/null
then
    echo "docker is not installed"
    exit
fi

if ! command -v mvn &> /dev/null
then
    echo "docker is not installed"
    exit
fi

docker-compose down
