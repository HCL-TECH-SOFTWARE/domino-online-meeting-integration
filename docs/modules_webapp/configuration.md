---
layout: default
title: Configuration
parent: DOMI OAuth Web Application
nav_order: 5
has_children: false
last_modified_date: 2021.03.09
---

Additional runtime configuration can be managed via a config.json file. The default configuration is:

```json
{
	"PORT": 8878,
	"METRICSPORT": 8879,
	
	"vertx": {
		
	},
	
	"TLSFile": "null",
	"TLSPassword": "null",
	"TLSType": "pfx",
	"cipher": {
		"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384": true,
		"TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384": true,
		"TLS_RSA_WITH_AES_256_GCM_SHA384": true
	},
	"enabledProtocols": {
		"TLSv1.3": false,
		"TLSv1.2": true
	},
	"removeInsecureProtocols": {
		"TLSv1": true,
		"TLSv1.1": true,
		"SSLv2Hello": true
	},
	"CORS": {
		"localhost": true,
		"hcl.com": true,
		".local": true
	},
	"prometheusMetrics": {
		"embeddedServerEndpoint": "/metrics",
		"enabled": true,
		"publishQuantiles": true,
		"startEmbeddedServer": true
	}
}
```

The application looks for additional configuration files in a directory called "domiconfig.d". To adjust the configuration, add a file config.json in this directory.