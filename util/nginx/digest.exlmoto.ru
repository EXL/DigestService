server {
	listen 80;
	server_name digest.exlmoto.ru;

	location / {
		proxy_pass http://127.0.0.1:8080;

		proxy_set_header Host $http_host;
		proxy_set_header X-Real-IP $remote_addr;
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
		proxy_set_header X-Forwarded-Proto $scheme;
	}

	# Reverse proxy.
	# To avoid stupid RKN blocks.
	# See additional information: http://www.nginx-discovery.com/2011/05/day-51-proxypass-and-resolver.html
	location ^~ /proxy {
		resolver 8.8.8.8;
		location ~ "^/proxy/(.*)/(.*)" {
			proxy_pass https://$1/$2;
		}
	}
}
