docker rm -f testServer
docker run --rm --name testServer -p 9009:25565 -d openjdk:17-oracle sleep 10000
docker cp forge testServer:/forge
docker cp build/libs testServer:/forge/mods
docker exec -ti -w /forge testServer ./run.sh
