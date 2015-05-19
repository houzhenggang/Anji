package com.anji.www.entry;

public class SceneInfo {
	private int sceneId;
	private String sceneName;
	private String memberId;// 所在会员ID
	private boolean isOn;

	/**
	 * 绑定硬件的地址
	 */
	private String ssuid;
	private String iconType;

	public int getSceneId() {
		return sceneId;
	}

	public void setSceneId(int sceneId) {
		this.sceneId = sceneId;
	}

	public String getSceneName() {
		return sceneName;
	}

	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getSsuid() {
		return ssuid;
	}

	public void setSsuid(String ssuid) {
		this.ssuid = ssuid;
	}

	public String getIconType() {
		return iconType;
	}

	public void setIconType(String iconType) {
		this.iconType = iconType;
	}

	public boolean isOn() {
		return isOn;
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}
}
