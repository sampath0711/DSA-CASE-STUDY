// Spotify - Listener Fatigue Analysis using Sparse Table
// DSA-2 Case Study | KL University

public class SpotifySkipAnalysis {

    // ─── Sparse Table ─────────────────────────────────────────────────────────
    static class SparseTable {

        int n;
        int[][] table;   // table[k][i] = min of 2^k elements starting at i
        int[] log2;      // precomputed floor(log2(i))

        SparseTable(int[] arr) {
            n = arr.length;
            int maxK = (int)(Math.log(n) / Math.log(2)) + 1;
            table = new int[maxK][n];
            log2  = new int[n + 1];

            // Precompute log2 table
            log2[1] = 0;
            for (int i = 2; i <= n; i++)
                log2[i] = log2[i / 2] + 1;

            // ── Build: k=0 (array itself) ─────────────────────────────────────
            System.out.println("\n[Build] Sparse Table — " + maxK
                    + " levels (k=0 to k=" + (maxK-1) + ") over n=" + n + " elements");
            System.out.println("\nk=0 (windows of size 1, i.e., the array itself):");
            for (int i = 0; i < n; i++) {
                table[0][i] = arr[i];
                System.out.printf("  i=%d: %d%n", i, table[0][i]);
            }

            // ── Build: k=1,2,3,... ───────────────────────────────────────────
            for (int k = 1; k < maxK; k++) {
                int len = 1 << k;  // 2^k
                System.out.printf("%nk=%d (min of %d consecutive):%n", k, len);
                for (int i = 0; i + (1 << k) <= n; i++) {
                    table[k][i] = Math.min(table[k-1][i], table[k-1][i + (1 << (k-1))]);
                    System.out.printf("  i=%d, len %d: %d%n", i, len, table[k][i]);
                }
            }
        }

        // ── O(1) Range Minimum Query ──────────────────────────────────────────
        int query(int l, int r) {
            int k = log2[r - l + 1];
            return Math.min(table[k][l], table[k][r - (1 << k) + 1]);
        }
    }

    // ─── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        // Sample skip-positions (8 most recent events, in seconds)
        int[] skipPos = {42, 18, 55, 9, 31, 22, 6, 47};

        System.out.println("=== Spotify: Listener Fatigue Analysis ===");
        System.out.println("\nWorkload:");
        System.out.println("  ~50 range-min queries per user per session");
        System.out.println("  ~3-5 skip-event updates per user per session");
        System.out.println("  Read-to-update ratio ≈ 10:1  → Sparse Table chosen over Segment Tree");

        System.out.print("\nSample skip-positions (seconds into track): [");
        for (int i = 0; i < skipPos.length; i++)
            System.out.print(skipPos[i] + (i < skipPos.length - 1 ? ", " : "]"));
        System.out.println();

        // ── Build Sparse Table ────────────────────────────────────────────────
        SparseTable st = new SparseTable(skipPos);

        // ── Range-Min Queries ─────────────────────────────────────────────────
        System.out.println("\n[Range-Min Queries] O(1) per query:");

        int[][] queries = {
            {0, 7},   // full array
            {0, 3},   // first half
            {4, 7},   // second half
            {1, 5},   // middle range
            {2, 6},   // another range
            {0, 1},   // two elements
        };

        for (int[] q : queries) {
            int l = q[0], r = q[1];
            int result = st.query(l, r);
            System.out.printf("  min(skip[%d..%d]) = %d%n", l, r, result);
        }

        // ── Fatigue Interpretation ────────────────────────────────────────────
        System.out.println("\n[Fatigue Analysis]");
        int minSkip = st.query(0, skipPos.length - 1);
        System.out.println("  Minimum skip-position across all recent events = "
                + minSkip + " seconds");
        System.out.println("  → User skipped very early (" + minSkip
                + "s) → High fatigue / impatience detected");
        System.out.println("  → Recommendations engine: serve shorter intros / familiar tracks");

        // ── Complexity Summary ────────────────────────────────────────────────
        System.out.println("\n=== Time Complexity Summary ===");
        System.out.println("  Build (preprocessing)  : O(n log n)");
        System.out.println("  Range-Min Query        : O(1)");
        System.out.println("  Update (periodic rebuild): O(n log n)");
        System.out.println();
        System.out.println("  Segment Tree Query     : O(log n)  — overkill for 10:1 read ratio");
        System.out.println("  Sparse Table Query     : O(1)      ✓ ideal for read-heavy workload");
        System.out.println("  n = 1024 users × skip buffer → Sparse Table handles all queries instantly");
    }
}
