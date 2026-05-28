#!/usr/bin/env bash
# Run all kapt-benchmark scenarios (3 workloads × 3 sizes = 9 scenarios)
# and print a per-scenario summary table.
#
# Requirements:
#   - gradle-profiler on PATH (https://github.com/gradle/gradle-profiler/releases)
#   - python3 (for the summary script)
#
# Usage (from the repository root):
#   ./scripts/run_all_benchmarks.sh
#
# Output:
#   profile-out/<workload>_<size>/benchmark.csv         (one per scenario)
#   profile-out/<workload>_<size>/benchmark.html        (one per scenario)
#
# Total wall time: ~3-4 hours on an M3 Max; size-dependent.

set -euo pipefail

cd "$(dirname "$0")/.."

if ! command -v gradle-profiler >/dev/null 2>&1; then
    echo "Error: gradle-profiler is not on PATH." >&2
    echo "Install via 'brew install gradle-profiler' or download from" >&2
    echo "https://github.com/gradle/gradle-profiler/releases" >&2
    exit 1
fi

if ! command -v python3 >/dev/null 2>&1; then
    echo "Error: python3 is not on PATH." >&2
    exit 1
fi

WORKLOADS=(synthetic dagger mapstruct)
SIZES=(small medium large)

echo "Running 9 scenarios (3 workloads × 3 sizes). This will take a few hours."
echo ""

csv_files=()
for w in "${WORKLOADS[@]}"; do
    for s in "${SIZES[@]}"; do
        echo "=== Running ${w}_${s} ==="
        gradle-profiler --benchmark \
            --output-dir "profile-out/${w}_${s}" \
            --scenario-file "scenarios/${w}.scenarios" \
            "${w}_${s}"
        csv_files+=("profile-out/${w}_${s}/benchmark.csv")
    done
done

echo ""
echo "=== Summary (all 9 scenarios) ==="
python3 scripts/summarize_benchmark.py "${csv_files[@]}"
