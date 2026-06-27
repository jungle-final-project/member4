from pathlib import Path

try:
    import yaml
except ModuleNotFoundError as exc:
    raise SystemExit("PyYAML is required to validate docs/openapi.yaml") from exc


OPENAPI_PATH = Path("docs/openapi.yaml")

REQUIRED_PATHS = [
    "/api/health",
    "/api/auth/login",
    "/api/users",
    "/api/auth/me",
    "/api/requirements/parse",
    "/api/builds/recommend",
    "/api/builds/{id}",
    "/api/builds/history",
    "/api/builds/{id}/change-part",
    "/api/parts",
    "/api/parts/{id}",
    "/api/tools/{tool}/check",
    "/api/price-alerts",
    "/api/agent-logs/upload",
    "/api/as-tickets",
    "/api/admin/dashboard",
]

POST_REQUEST_SCHEMAS = {
    "/api/builds/recommend": "BuildRecommendRequest",
    "/api/builds/{id}/change-part": "ChangePartRequest",
    "/api/tools/{tool}/check": "ToolCheckRequest",
    "/api/price-alerts": "PriceAlertCreateRequest",
    "/api/agent-logs/upload": "AgentLogUploadRequest",
    "/api/as-tickets": "AsTicketCreateRequest",
}


def main() -> None:
    with OPENAPI_PATH.open(encoding="utf-8") as file:
        spec = yaml.safe_load(file)

    if spec.get("openapi") != "3.0.3":
        raise SystemExit("docs/openapi.yaml must declare openapi: 3.0.3")

    paths = spec.get("paths", {})
    missing_paths = [path for path in REQUIRED_PATHS if path not in paths]
    if missing_paths:
        raise SystemExit(f"Missing OpenAPI paths: {', '.join(missing_paths)}")

    schemas = spec.get("components", {}).get("schemas", {})
    for path, schema_name in POST_REQUEST_SCHEMAS.items():
        post = paths.get(path, {}).get("post")
        if not post:
            raise SystemExit(f"Missing POST operation for {path}")

        request_body = post.get("requestBody")
        if not request_body:
            raise SystemExit(f"Missing requestBody for {path}")

        schema = (
            request_body.get("content", {})
            .get("application/json", {})
            .get("schema", {})
        )
        if schema.get("$ref") != f"#/components/schemas/{schema_name}":
            raise SystemExit(f"{path} must reference {schema_name}")

        if schema_name not in schemas:
            raise SystemExit(f"Missing schema: {schema_name}")

    print(f"OpenAPI validation passed: {len(paths)} paths")


if __name__ == "__main__":
    main()
