package com.absolute.am.model;


public class JobTask {
	private String m_name;
	private int m_percent;
	private JobTask[] m_subtasks;
	
	public JobTask(String name, int percent, int nrOfSubTasks) {
		m_name = name;
		m_percent = percent;
		if (nrOfSubTasks > 0) {
			m_subtasks = new JobTask[nrOfSubTasks];
		}
	}
	
	public void setSubtask(int index, JobTask jobTask) throws Exception {
		if (m_subtasks == null) {
			throw new Exception("JobTask: " + m_name + " : Cant add subtask to empty subtask list");
		}
		if (index < 0 || index >= m_subtasks.length) {
			throw new Exception("JobTask: " + m_name + " : Cant add subtask: index out of range, index = " + index);
		}
		m_subtasks[index] = jobTask;
	}
	
	/**
	 * The Name
	 */
	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	/**
	 * The job percent
	 */
	public int getPercent() {
		return m_percent;
	}

	public void setPercent(int percent) {
		this.m_percent = percent;
	}

	/**
	 * Subtask list
	 */
	public JobTask[] getSubtasks() {
		return m_subtasks;
	}

	public void setSubtasks(JobTask[] subtasks) {
		this.m_subtasks = subtasks;
	}

	public int calculatePercentCmpl() {
		if (m_percent != 100) {
			int tempPercent = 0;
			if (m_subtasks != null) {
				int nrOfSubtasks = m_subtasks.length;
				for (int i = 0; i < nrOfSubtasks; i++) {
					if (m_subtasks[i] != null) {
						tempPercent += m_subtasks[i].calculatePercentCmpl();
					}
				}
				m_percent =(int)Math.ceil((double)tempPercent / (double)nrOfSubtasks);
			}
		}
		return m_percent;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (m_subtasks != null) {
			for (int i = 0; i < m_subtasks.length; i++) {
				if (m_subtasks[i] != null) {
					sb.append(m_subtasks[i].toString());
				}
			}
		} else {
			sb.append("subTasks[] = null\n");
		}
		return sb.toString();
	}


}
