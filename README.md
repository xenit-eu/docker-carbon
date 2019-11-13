# Usage

This docker provides access to the Carbon/whisper layer of the graphite stack, inline with the one process per docker philosophy.

It is the intent to run this together with the hub.xenit.eu/graphiteweb, hub.xenit.eu/statsd and grafana dockers.

Make sure to map the whisper volume to the host for persistence of data.

Example

	docker run -d -v $PWD/whisper/:/opt/graphite/storage/whisper/ hub.xenit.eu/carbon
	
Default aggregation configuration is used, including 10 second periods for statsd. Override these in any derived dockers by replacing the storage-aggregation.conf and storage-schemas.conf configuration files.

The following two environment variables override the default behavior:

	MAX_UPDATES_PER_SECOND: Overrides the max amount of writebacks per second to storage.
	WHISPER_AUTOFLUSH: Default False. When set to True, carbon will do the buffering, not the kernel.
	MAX_CREATES_PER_MINUTE: Overrides the number of whisper files that get created each minute.
	
Example with the environment variables set at non-default values:
	
	docker run -d -v $PWD/whisper/:/opt/graphite/storage/whisper/ -e MAX_UPDATES_PER_SECOND=10 -e WHISPER_AUTOFLUSH=False hub.xenit.eu/carbon -e MAX_CREATES_PER_MINUTE=100
