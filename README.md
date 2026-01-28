# TMDB Sync

Servicio Spring Boot que sincroniza películas populares desde [The Movie Database (TMDB)](https://www.themoviedb.org/) hacia una base de datos PostgreSQL.

## Qué hace

1. Consume la API paginada de TMDB (`/3/movie/popular`)
2. Procesa cada película: clasifica por idioma original y computa un valor ajustado según el idioma
3. Persiste los resultados en PostgreSQL con lógica de upsert (inserta o actualiza)
4. Filtra contenido adulto y películas sin ratings

## Requisitos

- Docker y Docker Compose
- Token de TMDB

## Configuración

1. Clonar el repositorio

2. Crear el archivo `.env` a partir del ejemplo:

```bash
cp .env.example .env
```

3. Editar `.env` y poner tu token de TMDB:

```
TMDB_API_TOKEN=eyJhbGciOiJIUzI1NiJ9...
```

## Levantar la app

```bash
docker compose up --build
```

## Uso

Sincronizar películas (todas las páginas):

```bash
curl -X POST http://localhost:8080/api/sync
```

Sincronizar con límite de registros:

```bash
curl -X POST "http://localhost:8080/api/sync?limit=50"
```

Respuesta:

```json
{
  "processed": 47,
  "skipped": 3,
  "failed": 0
}
```