FROM ubuntu:18.04

MAINTAINER  Thijs Lemmens <thijs.lemmens@xenit.eu>

ARG CARBON_VERSION
ENV CARBON_VERSION ${CARBON_VERSION}
ARG TWISTED_VERSION
ENV TWISTED_VERSION ${TWISTED_VERSION}

RUN 	apt-get update && \
	apt-get -y install python python-pip && \
	rm -rf /var/lib/apt/lists/*

COPY      config /tmp/config

# Create users. Note carbon user has same uid as the one in visity/graphiteweb
RUN       addgroup --gid 30100 carbon
RUN       useradd -u 30106 -g carbon -s /bin/false carbon

RUN       pip install twisted==${TWISTED_VERSION} whisper==${CARBON_VERSION}
RUN       pip install --install-option="--prefix=/opt/graphite" carbon==${CARBON_VERSION}

# Copy configs into place and create needed dirs
COPY      config/ /opt/graphite/conf/

ENV       PYTHONPATH /opt/graphite/lib/
ENV       WHISPER_AUTOFLUSH False
ENV       MAX_UPDATES_PER_SECOND 500
ENV       MAX_CREATES_PER_MINUTE 50

# Entry point script also sets environment variables in config files.
COPY      docker-entrypoint.sh /

# Graphiteweb requires access to this folder and should be mounted to host
# for persistence.
VOLUME    /opt/graphite/storage/whisper/

EXPOSE    2003 2004 7002

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD       ["/opt/graphite/bin/carbon-cache.py", "--debug", "start"]
