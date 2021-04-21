package GA;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GA {

    private Chromosome[] chromosomes;
    private Chromosome[] nextGeneration;
    private int N;
    private int decisionMakingNum;
    private double p_c_t;
    private double p_m_t;
    private int MAX_GEN;
    private int bestProfit;
    private int[] bestDecisionMaking;
    private double bestFitness;
    private double[] averageFitness;
    private int[][] profit;
    private String filename;

    public GA() {
        N = 100;
        decisionMakingNum = 30;
        p_c_t = 0.9;
        p_m_t = 0.1;
        MAX_GEN = 1000;
        bestProfit = 0;
        bestDecisionMaking = new int[2];
        bestFitness = 0.0;
        averageFitness = new double[MAX_GEN];
        chromosomes = new Chromosome[N];
        profit = new int[decisionMakingNum][decisionMakingNum];

    }

    /**
     * Constructor of GA class
     *
     * @param n        种群规模
     * @param num      决策数量
     * @param g        运行代数
     * @param p_c      交叉率
     * @param p_m      变异率
     * @param filename 数据文件名
     */
    public GA(int n, int num, int g, double p_c, double p_m, String filename) {
        this.N = n;
        this.decisionMakingNum = num;
        this.MAX_GEN = g;
        this.p_c_t = p_c;
        this.p_m_t = p_m;
        bestDecisionMaking = new int[2];
        averageFitness = new double[MAX_GEN];
        bestFitness = 0.0;
        chromosomes = new Chromosome[N];
        nextGeneration = new Chromosome[N];
        profit = new int[decisionMakingNum][decisionMakingNum];
        this.filename = filename;
    }

    public BestData solve() throws IOException {
        System.out.println("---------------------Start initilization---------------------");
        init();
        System.out.println("---------------------End initilization---------------------");
        System.out.println("---------------------Start evolution---------------------");
        for (int i = 0; i < MAX_GEN; i++) {
            System.out.println("-----------Start generation " + i + "----------");
            evolve(i);
            System.out.println("-----------End generation " + i + "----------");
        }
        System.out.println("---------------------End evolution---------------------");
        BestData bestData = new BestData();
        bestData.setBestProfit(this.bestProfit);
        bestData.setBestDecisionMaking(this.bestDecisionMaking);
        return bestData;
    }

    /**
     * 初始化GA
     *
     * @throws IOException
     */
    @SuppressWarnings("resource")
    private void init() throws IOException {
        // 读取数据文件
        int[] x;
        int[] y;
        int[][] profit;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

        this.profit = new int[decisionMakingNum][decisionMakingNum];
        x = new int[decisionMakingNum];
        y = new int[decisionMakingNum];

        for (int i = 0; i < decisionMakingNum; i++) {
            String lineData = bufferedReader.readLine();
            String[] dataArr = lineData.split(" ");
            x[i] = Integer.valueOf(dataArr[1]).intValue();
            y[i] = Integer.valueOf(dataArr[2]).intValue();
        }
        // 计算利润
        for (int i = 0; i < decisionMakingNum ; i++) {
            for (int j = 0 ; j < decisionMakingNum ; j++) {
                double rij = Math.sqrt((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j]) * (y[i] - y[j]));
                int tij = (int) Math.round(rij);
                this.profit[i][j] = tij;
                this.profit[j][i] = this.profit[i][j];
            }
        }
        for (int i = 0; i < N; i++) {
            Chromosome chromosome = new Chromosome(decisionMakingNum, this.profit);
            chromosome.randomGeneration();
            chromosomes[i] = chromosome;
            chromosome.print();
        }
    }
    private void evolve(int g) {
        double[] selectionP = new double[N];// 选择概率
        double sum = 0.0;
        double tmp = 0.0;
        for (int i = 0; i < N; i++) {
            sum += chromosomes[i].getFitness();
            if (chromosomes[i].getFitness() > bestFitness) {
                bestFitness = chromosomes[i].getFitness();
                bestProfit = (int) (1.0 / bestFitness);
            }
        }
        averageFitness[g] = sum / N;

        System.out.println("The average fitness in " + g + " generation is: " + averageFitness[g]
                + ", and the best fitness is: " + bestFitness);
        for (int i = 0; i < N; i++) {
            tmp += chromosomes[i].getFitness() / sum;
            selectionP[i] = tmp;
        }
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < N; i = i + 2) {
            Chromosome[] children = new Chromosome[2];
            // 轮盘赌选择两个染色体
            for (int j = 0; j < 2; j++) {

                int selectStrategy = 0;
                for (int k = 0; k < N - 1; k++) {
                    double p = random.nextDouble();
                    if (p > selectionP[k] && p <= selectionP[k + 1]) {
                        selectStrategy = k;
                    }
                    if (k == 0 && random.nextDouble() <= selectionP[k]) {
                        selectStrategy = 0;
                    }
                }
                try {
                    children[j] = (Chromosome) chromosomes[selectStrategy].clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

            // 交叉操作
            if (random.nextDouble() < p_c_t) {
                // System.out.println("crossover");
                // random = new Random(System.currentTimeMillis());
                // 定义两个cut点
                int cutPoint1 = -1;
                int cutPoint2 = -1;
                int r1 = random.nextInt(decisionMakingNum);
                if (r1 > 0 && r1 < decisionMakingNum - 1) {
                    cutPoint1 = r1;
                    // random = new Random(System.currentTimeMillis());
                    int r2 = random.nextInt(decisionMakingNum - r1);
                    if (r2 == 0) {
                        cutPoint2 = r1 + 1;
                    } else if (r2 > 0) {
                        cutPoint2 = r1 + r2;
                    }
                }
                if (cutPoint1 > 0 && cutPoint2 > 0) {
                    int[] tour1 = new int[decisionMakingNum];
                    int[] tour2 = new int[decisionMakingNum];
                    if (cutPoint2 == decisionMakingNum - 1) {
                        for (int j = 0; j < decisionMakingNum; j++) {
                            tour1[j] = children[0].getTour()[j];
                            tour2[j] = children[1].getTour()[j];
                        }
                    } else {
                        // int n = 1;
                        for (int j = 0; j < decisionMakingNum; j++) {
                            if (j < cutPoint1) {
                                tour1[j] = children[0].getTour()[j];
                                tour2[j] = children[1].getTour()[j];
                            } else if (j >= cutPoint1 && j < cutPoint1 + decisionMakingNum - cutPoint2 - 1) {
                                tour1[j] = children[0].getTour()[j + cutPoint2 - cutPoint1 + 1];
                                tour2[j] = children[1].getTour()[j + cutPoint2 - cutPoint1 + 1];
                            } else {
                                tour1[j] = children[0].getTour()[j - decisionMakingNum + cutPoint2 + 1];
                                tour2[j] = children[1].getTour()[j - decisionMakingNum + cutPoint2 + 1];
                            }

                        }
                    }
                    for (int j = 0; j < decisionMakingNum; j++) {
                        if (j < cutPoint1 || j > cutPoint2) {

                            children[0].getTour()[j] = -1;
                            children[1].getTour()[j] = -1;
                        } else {
                            int tmp1 = children[0].getTour()[j];
                            children[0].getTour()[j] = children[1].getTour()[j];
                            children[1].getTour()[j] = tmp1;
                        }
                    }
                    if (cutPoint2 == decisionMakingNum - 1) {
                        int position = 0;
                        for (int j = 0; j < cutPoint1; j++) {
                            for (int m = position; m < decisionMakingNum; m++) {
                                boolean flag = true;
                                for (int n = 0; n < decisionMakingNum; n++) {
                                    if (tour1[m] == children[0].getTour()[n]) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {

                                    children[0].getTour()[j] = tour1[m];
                                    position = m + 1;
                                    break;
                                }
                            }
                        }
                        position = 0;
                        for (int j = 0; j < cutPoint1; j++) {
                            for (int m = position; m < decisionMakingNum; m++) {
                                boolean flag = true;
                                for (int n = 0; n < decisionMakingNum; n++) {
                                    if (tour2[m] == children[1].getTour()[n]) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    children[1].getTour()[j] = tour2[m];
                                    position = m + 1;
                                    break;
                                }
                            }
                        }

                    } else {

                        int position = 0;
                        for (int j = cutPoint2 + 1; j < decisionMakingNum; j++) {
                            for (int m = position; m < decisionMakingNum; m++) {
                                boolean flag = true;
                                for (int n = 0; n < decisionMakingNum; n++) {
                                    if (tour1[m] == children[0].getTour()[n]) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    children[0].getTour()[j] = tour1[m];
                                    position = m + 1;
                                    break;
                                }
                            }
                        }
                        for (int j = 0; j < cutPoint1; j++) {
                            for (int m = position; m < decisionMakingNum; m++) {
                                boolean flag = true;
                                for (int n = 0; n < decisionMakingNum; n++) {
                                    if (tour1[m] == children[0].getTour()[n]) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    children[0].getTour()[j] = tour1[m];
                                    position = m + 1;
                                    break;
                                }
                            }
                        }

                        position = 0;
                        for (int j = cutPoint2 + 1; j < decisionMakingNum; j++) {
                            for (int m = position; m < decisionMakingNum; m++) {
                                boolean flag = true;
                                for (int n = 0; n < decisionMakingNum; n++) {
                                    if (tour2[m] == children[1].getTour()[n]) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    children[1].getTour()[j] = tour2[m];
                                    position = m + 1;
                                    break;
                                }
                            }
                        }
                        for (int j = 0; j < cutPoint1; j++) {
                            for (int m = position; m < decisionMakingNum; m++) {
                                boolean flag = true;
                                for (int n = 0; n < decisionMakingNum; n++) {
                                    if (tour2[m] == children[1].getTour()[n]) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    children[1].getTour()[j] = tour2[m];
                                    position = m + 1;
                                    break;
                                }
                            }
                        }
                    }

                }
            }

            // 变异操作(DM)
            if (random.nextDouble() < p_m_t) {
                // System.out.println("mutation");
                for (int j = 0; j < 2; j++) {
                    // random = new Random(System.currentTimeMillis());
                    // 定义两个cut点
                    int cutPoint1 = -1;
                    int cutPoint2 = -1;
                    int r1 = random.nextInt(decisionMakingNum);
                    if (r1 > 0 && r1 < decisionMakingNum - 1) {
                        cutPoint1 = r1;
                        // random = new Random(System.currentTimeMillis());
                        int r2 = random.nextInt(decisionMakingNum - r1);
                        if (r2 == 0) {
                            cutPoint2 = r1 + 1;
                        } else if (r2 > 0) {
                            cutPoint2 = r1 + r2;
                        }

                    }

                    if (cutPoint1 > 0 && cutPoint2 > 0) {
                        List<Integer> tour = new ArrayList<Integer>();
                        if (cutPoint2 == decisionMakingNum - 1) {
                            for (int k = 0; k < cutPoint1; k++) {
                                tour.add(Integer.valueOf(children[j].getTour()[k]));
                            }
                        } else {
                            for (int k = 0; k < decisionMakingNum; k++) {
                                if (k < cutPoint1 || k > cutPoint2) {
                                    tour.add(Integer.valueOf(children[j].getTour()[k]));
                                }
                            }
                        }
                        int position = random.nextInt(tour.size());
                        if (position == 0) {
                            for (int k = cutPoint2; k >= cutPoint1; k--) {
                                tour.add(0, Integer.valueOf(children[j].getTour()[k]));
                            }
                        } else if (position == tour.size() - 1) {
                            for (int k = cutPoint1; k <= cutPoint2; k++) {
                                tour.add(Integer.valueOf(children[j].getTour()[k]));
                            }

                        } else {
                            for (int k = cutPoint1; k <= cutPoint2; k++) {
                                tour.add(position, Integer.valueOf(children[j].getTour()[k]));
                            }
                        }

                        for (int k = 0; k < decisionMakingNum; k++) {
                            children[j].getTour()[k] = tour.get(k).intValue();
                        }
                    }

                }
            }
            nextGeneration[i] = children[0];
            nextGeneration[i + 1] = children[1];

        }

        for (int k = 0; k < N; k++) {
            try {
                chromosomes[k] = (Chromosome) nextGeneration[k].clone();
            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void printOptimal() {
        System.out.println("The best fitness is: " + bestFitness);
        System.out.println("The best tour length is: " + bestProfit);
        System.out.println("The best tour is: ");

        System.out.print(bestDecisionMaking[0]);
        for (int i = 1; i < decisionMakingNum; i++) {
            System.out.print("->" + bestDecisionMaking[i]);
        }
    }

    private void outputResults() {
        String filename = "result.txt";
        try {
            @SuppressWarnings("resource")
            FileOutputStream outputStream = new FileOutputStream(filename);
            for (int i = 0; i < averageFitness.length; i++) {
                String line = String.valueOf(averageFitness[i]) + "\r\n";
                outputStream.write(line.getBytes());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Chromosome[] getChromosomes() {
        return chromosomes;
    }

    public void setChromosomes(Chromosome[] chromosomes) {
        this.chromosomes = chromosomes;
    }

    public int getDecisionMakingNum() {
        return decisionMakingNum;
    }

    public void setDecisionMakingNum(int decisionMakingNum) {
        this.decisionMakingNum = decisionMakingNum;
    }

    public double getP_c_t() {
        return p_c_t;
    }

    public void setP_c_t(double p_c_t) {
        this.p_c_t = p_c_t;
    }

    public double getP_m_t() {
        return p_m_t;
    }

    public void setP_m_t(double p_m_t) {
        this.p_m_t = p_m_t;
    }

    public int getMAX_GEN() {
        return MAX_GEN;
    }

    public void setMAX_GEN(int mAX_GEN) {
        MAX_GEN = mAX_GEN;
    }

    public int getBestProfit() {
        return bestProfit;
    }

    public void setBestProfit(int bestProfit) {
        this.bestProfit = bestProfit;
    }

    public int[] getBestDecisionMaking() {
        return bestDecisionMaking;
    }

    public void setBestDecisionMaking(int[] bestDecisionMaking) {
        this.bestDecisionMaking = bestDecisionMaking;
    }

    public double[] getAverageFitness() {
        return averageFitness;
    }

    public void setAverageFitness(double[] averageFitness) {
        this.averageFitness = averageFitness;
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public int[][] getProfit() {
        return profit;
    }

    public void setProfit(int[][] profit) {
        this.profit = profit;
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        GA ga = new GA(100, 51, 100, 0.95, 0.75, "resources/eil51.txt");
        BestData solve = ga.solve();

    }

}