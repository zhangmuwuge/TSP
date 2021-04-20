package ACO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Ant implements Cloneable {

	private List<Integer> tabu; // 禁忌表
	private List<Integer> allowedDecisionMaking; // 允许搜索的策略
	private float[][] delta; // 信息数变化矩阵
	private int[][] distance; // 距离矩阵

	private float alpha;
	private float beta;

	private int index; // 策略组合下标
	private int portNum = 2; // 港口数量
	private int decisionMakingNum; // 决策数量

	private int first; // 起始策略
	private int current; // 当前策略

	public Ant() {
		this.decisionMakingNum = 30;
		index = 0;

	}

	/**
	 * Constructor of Ant
	 * 
	 * @param portNum
	 *            蚂蚁数量
	 */
	public Ant(int decisionMakingNum) {
		this.decisionMakingNum = decisionMakingNum;
		index = 0;

	}

	/**
	 * 初始化蚂蚁，随机选择起始位置
	 * 
	 * @param distance
	 *            距离矩阵
	 * @param a
	 *            alpha
	 * @param b
	 *            beta
	 */
	public void init(int[][] distance, float a, float b) {
		alpha = a;
		beta = b;
		allowedDecisionMaking = new ArrayList<>();
		tabu = new ArrayList<>();
		this.distance = distance;
		delta = new float[portNum][portNum];
		for (int i = 0; i < portNum; i++) {
			Integer integer = new Integer(i);
			allowedDecisionMaking.add(integer);
			for (int j = 0; j < portNum; j++) {
				delta[i][j] = 0.f;
			}
		}

		Random random = new Random(System.currentTimeMillis());
		first = random.nextInt(portNum);
		for (Integer i : allowedDecisionMaking) {
			if (i.intValue() == first) {
				allowedDecisionMaking.remove(i);
				break;
			}
		}

		tabu.add(Integer.valueOf(first));
		current = first;
	}

	/**
	 * 选择下一个城市
	 * 
	 * @param pheromone
	 *            信息素矩阵
	 */
	public void selectNextDecisionMaking(float[][] pheromone) {
		float[] p = new float[decisionMakingNum];
		float sum = 0.0f;
		// 计算分母部分
		for (Integer i : allowedDecisionMaking) {
			sum += Math.pow(pheromone[portNum][i.intValue()], alpha)
					* Math.pow(1.0 / distance[portNum][i.intValue()], beta);
		}
		// 计算概率矩阵
		for (int i = 0; i < portNum; i++) {
			boolean flag = false;
			for (Integer j : allowedDecisionMaking) {
				if (i == j.intValue()) {
					p[i] = (float) (Math.pow(pheromone[current][i], alpha)
							* Math.pow(1.0 / distance[current][i], beta)) / sum;
					flag = true;
					break;
				}
			}
			if (flag == false) {
				p[i] = 0.f;
			}
		}
		// 轮盘赌选择下一个策略
		Random random = new Random(System.currentTimeMillis());
		float sleectP = random.nextFloat();
		int selectCity = 0;
		float sum1 = 0.f;
		for (int i = 0; i < decisionMakingNum; i++) {
			sum1 += p[i];
			if (sum1 >= sleectP) {
				selectCity = i;
				break;
			}
		}

		// 从允许选择的城市中去除select city
		for (Integer i : allowedDecisionMaking) {
			if (i.intValue() == selectCity) {
				allowedDecisionMaking.remove(i);
				break;
			}
		}
		// 在禁忌表中添加select city
		tabu.add(Integer.valueOf(selectCity));
		// 将当前城市改为选择的城市
		current = selectCity;
	}

	/**
	 * 计算路径长度
	 * 
	 * @return 路径长度
	 */
	private int calculateTourLength() {
		int len = 0;
		for (int i = 0; i < portNum; i++) {
			len += distance[this.tabu.get(i).intValue()][this.tabu.get(i + 1).intValue()];
		}
		return len;
	}

	public List<Integer> getAllowedDecisionMaking() {
		return allowedDecisionMaking;
	}

	public void setAllowedDecisionMaking(List<Integer> allowedDecisionMaking) {
		this.allowedDecisionMaking = allowedDecisionMaking;
	}

	public int getIndex() {
		index = calculateTourLength();
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getPortNum() {
		return portNum;
	}

	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}

	public List<Integer> getTabu() {
		return tabu;
	}

	public void setTabu(Vector<Integer> tabu) {
		this.tabu = tabu;
	}

	public float[][] getDelta() {
		return delta;
	}

	public void setDelta(float[][] delta) {
		this.delta = delta;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

}