from __future__ import annotations

import argparse
import json
import random
from datetime import datetime, timedelta, timezone
from pathlib import Path

try:
    import psutil
except Exception:  # pragma: no cover - optional for prototype environments
    psutil = None

KST = timezone(timedelta(hours=9))


def metric_snapshot(ts: datetime, index: int) -> dict:
    if psutil:
        cpu_usage = psutil.cpu_percent(interval=0.05)
        ram_usage = psutil.virtual_memory().percent
        disk_usage = psutil.disk_usage("/").percent
    else:
        cpu_usage = 38 + index * 3 + random.random() * 8
        ram_usage = 62 + index * 2 + random.random() * 6
        disk_usage = 49 + random.random()

    return {
        "timestamp": ts.isoformat(),
        "cpuUsage": round(cpu_usage, 1),
        "ramUsage": round(ram_usage, 1),
        "gpuUsage": round(min(98, 64 + index * 4 + random.random() * 8), 1),
        "vramUsage": round(min(95, 58 + index * 3 + random.random() * 5), 1),
        "gpuTemp": round(min(91, 70 + index * 1.8 + random.random() * 3), 1),
        "cpuTemp": round(min(86, 62 + index * 1.2 + random.random() * 2), 1),
        "diskUsage": round(disk_usage, 1),
        "osErrorEvent": None if index % 7 else "Display driver warning",
        "topCpuProcess": "game.exe" if index % 2 else "ide64.exe",
        "topRamProcess": "game.exe",
    }


def write_sample(out: Path, count: int, interval_seconds: int) -> None:
    out.parent.mkdir(parents=True, exist_ok=True)
    start = datetime.now(KST) - timedelta(seconds=count * interval_seconds)
    with out.open("w", encoding="utf-8") as file:
        for index in range(count):
            row = metric_snapshot(start + timedelta(seconds=index * interval_seconds), index)
            file.write(json.dumps(row, ensure_ascii=False) + "\n")


def export_recent(source: Path, out: Path, minutes: int) -> None:
    cutoff = datetime.now(KST) - timedelta(minutes=minutes)
    rows: list[dict] = []
    with source.open("r", encoding="utf-8") as file:
        for line in file:
            if not line.strip():
                continue
            row = json.loads(line)
            try:
                ts = datetime.fromisoformat(row["timestamp"])
            except ValueError:
                continue
            if ts >= cutoff:
                rows.append(row)

    out.parent.mkdir(parents=True, exist_ok=True)
    with out.open("w", encoding="utf-8") as file:
        for row in rows:
            file.write(json.dumps(row, ensure_ascii=False) + "\n")


def main() -> None:
    parser = argparse.ArgumentParser(description="BuildGraph AI PC Agent prototype CLI")
    sub = parser.add_subparsers(dest="command", required=True)

    sample = sub.add_parser("sample", help="generate sample JSONL hardware metrics")
    sample.add_argument("--out", type=Path, default=Path("sample-agent-log.jsonl"))
    sample.add_argument("--count", type=int, default=24)
    sample.add_argument("--interval-seconds", type=int, default=5)

    export = sub.add_parser("export", help="export recent JSONL rows")
    export.add_argument("--source", type=Path, required=True)
    export.add_argument("--out", type=Path, default=Path("recent-30m.jsonl"))
    export.add_argument("--minutes", type=int, default=30)

    args = parser.parse_args()

    if args.command == "sample":
        write_sample(args.out, args.count, args.interval_seconds)
        print(f"wrote {args.out}")
    elif args.command == "export":
        export_recent(args.source, args.out, args.minutes)
        print(f"exported {args.out}")


if __name__ == "__main__":
    main()
