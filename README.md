HN-Subscribe
============

Keyword-based email subscriptions to Hacker News using Storm

## Dependencies

- [Storm](http://storm-project.net/)
- [storm-redis-pubsub](https://github.com/stormprocessor/storm-redis-pubsub)
- [Jsoup](http://jsoup.org/)

## Setup

- [Install](http://leiningen.org/#install) leiningen
- `$ lein deps`
- `$ lein javac`
- `$ java -cp $(lein classpath) storm.hnsubscribe.PostTopology`

## Todo

- Implement PostTopology using [storm-redis-pubsub](https://github.com/sorenmacbeth/storm-redis-pubsub)
- Hook into email provider
- Go from local cluster setup to the real deal
- ...
