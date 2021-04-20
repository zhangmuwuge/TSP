package ACO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Ant implements Cloneable {

	private List<Integer> tabu; // ���ɱ�
	private List<Integer> allowedDecisionMaking; // ���������Ĳ���
	private float[][] delta; // ��Ϣ���仯����
	private int[][] distance; // �������

	private float alpha;
	private float beta;

	private int index; // ��������±�
	private int portNum = 2; // �ۿ�����
	private int decisionMakingNum; // ��������

	private int first; // ��ʼ����
	private int current; // ��ǰ����

	public Ant() {
		this.decisionMakingNum = 30;
		index = 0;

	}

	/**
	 * Constructor of Ant
	 * 
	 * @param portNum
	 *            ��������
	 */
	public Ant(int decisionMakingNum) {
		this.decisionMakingNum = decisionMakingNum;
		index = 0;

	}

	/**
	 * ��ʼ�����ϣ����ѡ����ʼλ��
	 * 
	 * @param distance
	 *            �������
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
	 * ѡ����һ������
	 * 
	 * @param pheromone
	 *            ��Ϣ�ؾ���
	 */
	public void selectNextDecisionMaking(float[][] pheromone) {
		float[] p = new float[decisionMakingNum];
		float sum = 0.0f;
		// �����ĸ����
		for (Integer i : allowedDecisionMaking) {
			sum += Math.pow(pheromone[portNum][i.intValue()], alpha)
					* Math.pow(1.0 / distance[portNum][i.intValue()], beta);
		}
		// ������ʾ���
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
		// ���̶�ѡ����һ������
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

		// ������ѡ��ĳ�����ȥ��select city
		for (Integer i : allowedDecisionMaking) {
			if (i.intValue() == selectCity) {
				allowedDecisionMaking.remove(i);
				break;
			}
		}
		// �ڽ��ɱ������select city
		tabu.add(Integer.valueOf(selectCity));
		// ����ǰ���и�Ϊѡ��ĳ���
		current = selectCity;
	}

	/**
	 * ����·������
	 * 
	 * @return ·������
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