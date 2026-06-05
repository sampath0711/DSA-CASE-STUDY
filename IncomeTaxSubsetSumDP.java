public class IncomeTaxSubsetSumDP {

    public static void main(String[] args) {

        int[] deductions = {50000, 30000, 25000, 15000, 12000, 8000, 5000};
        int target = 100000;
        int n = deductions.length;

        boolean[][] dp = new boolean[n + 1][target + 1];

        for (int i = 0; i <= n; i++) {
            dp[i][0] = true;
        }

        for (int i = 1; i <= n; i++) {
            for (int sum = 1; sum <= target; sum++) {
                if (deductions[i - 1] <= sum) {
                    dp[i][sum] = dp[i - 1][sum] || dp[i - 1][sum - deductions[i - 1]];
                } else {
                    dp[i][sum] = dp[i - 1][sum];
                }
            }
        }

        System.out.println("=== Income-Tax Bracket Optimisation using Subset Sum DP ===");

        System.out.print("\nDeduction Values: ");
        for (int value : deductions) {
            System.out.print(value + " ");
        }

        System.out.println("\nTarget Amount: " + target);

        if (dp[n][target]) {
            System.out.println("\nSubset exists for target amount.");

            System.out.print("Selected Deductions: ");

            int sum = target;
            for (int i = n; i > 0 && sum > 0; i--) {
                if (!dp[i - 1][sum]) {
                    System.out.print(deductions[i - 1] + " ");
                    sum = sum - deductions[i - 1];
                }
            }

            System.out.println("\nTotal Deduction = " + target);
        } else {
            System.out.println("\nNo subset exists for target amount.");
        }

        System.out.println("\n=== Time Complexity Summary ===");
        System.out.println("DP Table Construction : O(n * target)");
        System.out.println("Subset Backtracking   : O(n)");
        System.out.println("Space Complexity      : O(n * target)");
    }
}
