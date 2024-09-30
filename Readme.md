# Cardano Bolnisi Aggregator
This repository implements an aggregator application for the Cardano Foundation Bolnisi Georgian Wine pilot program.

For the pilot, bottles produced by wineries in the Bolnisi region of Georgia will have QR codes attached that link to off-chain data.
The transactions will be published on-chain and serves as verification.

The aggregator will index the on-chain data, retrieve the off-chain data and aggregate the number of bottles, number of wineries and number of certificates. 
These numbers will be saved in a database and can be used by other applications. 

To index the data [yaci-store](https://github.com/bloxbean/yaci-store) is used. It is a light weight and modular indexer.

## How to run
1. Clone the repository
2. Fill `.env` file with your configuration
3. Build the docker container with `docker build -t cardano-bolnisi-aggregator .`
   * A public pre-build docker container will be published soon
5. Run the container with `docker run -d --env-file .env cardano-bolnisi-aggregator`

## Configuration
The configuration is done via the `.env` file. The following variables are available:
* `PROTOCOL_MAGIC` - The protocol magic of the Cardano network. Default is `764824073` for the mainnet.
* `CARDANO_NODE_HOST` - The host of the Cardano node. Default is `backbone.mainnet.cardanofoundation.org`.
* `CARDANO_NODE_PORT` - The port of the Cardano node. Default is `3001`.
* `DB_DRIVER` - The database driver. Default is `postgres`.
* `DB_HOST` - The host of the database. Default is `localhost`.
* `DB_PORT` - The port of the database. Default is `5432`.
* `DB_USER` - The user of the database. Default is `bolnisiagg`.
* `DB_PASSWORD` - The password of the database. Default is `bolnisiagg`.
* `DB_NAME` - The name of the database. Default is `bolnisiagg`.
* `DB_SCHEMA` - The schema of the database. Default is `bolnisi`.
* `INDEXER_START_SLOT` - The slot to start indexing from. Default is `109254210` (First Bolnisi Transaction).
* `INDEXER_START_HASH` - The hash of the block to start indexing from. Default is `bcd4e8ae24557a526e4243574d53a2e9e1268653e2b73e1c99048d3959edd404` (First Bolnisi Transaction).
* `BOLNISI_RESOLVER_URL` - The URL of the Bolnisi resolver. Default is `https://offchain.pro.cf-bolnisi-mainnet.eu-west-1.bnwa.metadata.dev.cf-deployments.org/api/v1/storage/objectUrl/georgian-wine/`.