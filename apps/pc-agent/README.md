# BuildGraph PC Agent Skeleton

Prototype-only Python CLI for generating JSON Lines hardware logs and exporting a recent window for AS upload tests.

## Commands

```powershell
python buildgraph_agent.py sample --out ../../seed/sample-agent-log.jsonl
python buildgraph_agent.py export --source ../../seed/sample-agent-log.jsonl --out recent-30m.jsonl --minutes 30
```

The MVP skeleton does not install a background service. It only creates deterministic local logs for frontend/backend AS flow testing.
