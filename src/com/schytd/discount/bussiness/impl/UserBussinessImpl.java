package com.schytd.discount.bussiness.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.dao.UserDao;
import com.schytd.discount.dao.impl.UserDaoImp;
import com.schytd.discount.enties.ConsumHistory;
import com.schytd.discount.enties.ConsumHistoryItem;
import com.schytd.discount.enties.ConsumptionTimesAndAmount;
import com.schytd.discount.enties.FinalValue;
import com.schytd.discount.enties.IntroductionInfo;
import com.schytd.discount.enties.Level;
import com.schytd.discount.enties.ScoreGetHistory;
import com.schytd.discount.enties.ScoreGetHistoryItem;
import com.schytd.discount.enties.UseScoreHistory;
import com.schytd.discount.enties.UseScoreHistoryItem;
import com.schytd.discount.enties.User;
import com.schytd.discount.enties.UserSessionId;
import com.schytd.discount.net.UserNet;
import com.schytd.discount.net.impl.UserNetImpl;
import com.schytd.discount.tools.ImageTools;
import com.schytd.discount.tools.NetTools;
import com.schytd.discount.tools.StrTools;

public class UserBussinessImpl implements UserBussiness {
	private UserDao mUserDao;

	private UserNet mUserNet;
	private String sessinId = null;

	public UserBussinessImpl() {
		mUserNet = new UserNetImpl();
	}

	public UserBussinessImpl(Context context) {
		mUserDao = new UserDaoImp(context);
		mUserNet = new UserNetImpl();
	}

	@Override
	public String userRegister(String... params) throws Exception {
		String result = null;
		// 注册
		result = mUserNet.toRegiester("1.0", "user.doreg", "android_app",
				params[0], params[1], params[2], params[3]);
		return result;
	}

	/**
	 * @params 电话号码
	 */
	@Override
	public List<String> userLogin(String... params) throws Exception {
		List<String> result;
		result = mUserNet.toLogin("1.0", "user.dologin", "android_app",
				params[0], params[1]);
		return result;
	}

	@Override
	public Boolean userIsRegister(String params) throws Exception {
		String result = null;
		// 是否注册
		result = mUserNet.isRegiester("1.0", "user.isReged", "android_app",
				params);
		if (StrTools.isNull(result)) {
			return false;
		}
		if (!result.equals("0")) {
			return true;
		}
		return false;
	}

	// 获得验证码
	@Override
	public Boolean getCaptchaCode(String params) throws Exception {
		String result = null;
		// 获取验证码是否成功
		result = mUserNet.getCode("1.0", "user.sendcaptcha", "android_app",
				params);
		if (StrTools.isNull(result)) {
			return false;
		}
		if (result.equals("0")) {
			return true;
		}
		return false;
	}

	@Override
	public void newUserSessionId(UserSessionId user) throws Exception {
		mUserDao.insertUserSessionId(user);
	}

	@Override
	public UserSessionId getUserSessionId() throws Exception {
		UserSessionId sessionId = mUserDao.selectUserSessionId();
		return sessionId;
	}
	@Override
	public void newUser(User user) throws Exception {
		mUserDao.insertUser(user);
	}

	private static User user = null;

	// 获得用户信息
	@Override
	public User getUserinfo(final String sessionId) throws Exception {
		user = mUserDao.selectUser();
		// 没有sessionId
		if (StrTools.isNull(sessionId)) {
			throw new RuntimeException("");
		}
		// 数据没有该用户的缓存 从网络端获取
		if (user.getName() == null || user.getNum() == null
				|| user.getGender() == null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						user = mUserNet.getUserInfo("1.0",
								"user.getUserBaseInfo", "android_app",
								sessionId);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			// 并且再次存入数据库
			mUserDao.delteUser();
			mUserDao.insertUser(user);
		}
		return user;
	}

	// 获得用户信息
	@Override
	public User getUserinfo() throws Exception {
		user = mUserDao.selectUser();
		// 没有sessionId
		sessinId = getSessionId();
		if (sessinId == null) {
			return null;
		}
		// 数据没有该用户的缓存 从网络端获取
		if (user==null||user.getName() == null || user.getNum() == null
				|| user.getGender() == null) {
			user = mUserNet.getUserInfo("1.0", "user.getUserBaseInfo",
					"android_app", sessinId);
			// 并且再次存入数据库
			mUserDao.delteUser();
			mUserDao.insertUser(user);
		}
		return user;
	}

	// 调用时 如果返回结果为false 再次调用
	@Override
	public boolean removeUser() throws Exception {
		int i = -1;
		i = mUserDao.delteUser();
		if (i < 0) {
			return true;
		}
		return false;
	}

	// 传入三个参数 修改用户信息
	@Override
	public boolean alterUserInfo(String... params) throws Exception {
		String result = null;
		String gender = null;
		sessinId = getSessionId();
		String type = params[0];
		if (type.equals("1")) {
			// 修改昵称
			result = mUserNet.UpdateUserInfo("1.0", "user.updateUserBaseInfo",
					"android_app", "1", params[1], sessinId);
			// 存入数据库
			if (result.equals("0")) {
				mUserDao.updateUserInfo(1, "name", params[1]);
			}

		} else if (type.equals("2")) {
			// 修改性别
			result = mUserNet.UpdateUserInfo("1.0", "user.updateUserBaseInfo",
					"android_app", "2", params[1], sessinId, params[2]);
			// 存入数据库
			if (params[1].equals("1")) {
				gender = "男";
			} else if (params[1].equals("2")) {
				gender = "女";
			}
			if (result.equals("0")) {
				mUserDao.updateUserInfo(1, "gender", gender);
			}
		} else if (type.equals("3")) {
			// 修改密码
			result = mUserNet.UpdateUserInfo("1.0", "user.updateUserBaseInfo",
					"android_app", "3", params[1], params[2], sessinId);
			// 存入数据库
			if (result.equals("0")) {
				mUserDao.updateUserLoginInfo("password", params[2]);
			}
		}
		if (!StrTools.isNull(result)) {
			if (result.equals("0")) {
				return true;
			}
		}
		return false;
	}

	// 忘记密码 修改
	@Override
	public boolean forgetUserPassword(String... params) throws Exception {
		String result = null;
		String newPassword = NetTools.getMD5Code(params[0].concat(params[2])
				.trim().getBytes());
		// 传向网络端修改密码
		result = mUserNet.reSetUserPassword("1.0", "user.resetPassword",
				"android_app", params[0], params[1], newPassword);
		if (result.equals("0")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public UserSessionId getUserSessionId(Integer id) throws Exception {
		UserSessionId userSessionId = null;
		userSessionId = mUserDao.selectUserSessionId();
		if (userSessionId != null) {
			return userSessionId;
		}
		return null;
	}

	public String getSessionId() throws Exception {
		UserSessionId userSessionId = null;
		String sessionId = null;
		userSessionId = getUserSessionId();
		long result = 0;
		if (userSessionId != null) {
			sessionId = userSessionId.getSessionid();
			long time = userSessionId.getTime();
			result = System.currentTimeMillis() - time;
			if (result >= 3600000 || sessionId.equals("0")) {
				removeSessionId();
				User user = mUserDao.selectUserLoginInfo();
				String num = user.getNum();
				String password = user.getPassword();
				sessionId = userLogin(num, password).get(1);
				// 封装newSessionId对象
				UserSessionId newSessionId = new UserSessionId();
				newSessionId.setId(1);
				newSessionId.setSessionid(sessionId);
				newSessionId.setTime(System.currentTimeMillis());
				newUserSessionId(newSessionId);
				return sessionId;
			} else {
				return sessionId;
			}
		} else {
			return sessionId;
		}
	}

	@Override
	public int removeSessionId() throws Exception {
		int i = 0;
		i = mUserDao.delteUserSessionId();
		return i;
	}

	@Override
	public User getUser() throws Exception {
		User user = null;
		user = mUserDao.selectUser();
		return user;
	}

	// 获取消费总金额和总次数
	@Override
	public ConsumptionTimesAndAmount getConsumInfo() throws Exception {
		String sessionId = null;
		sessionId = getSessionId();
		return mUserNet.getConsumforService(sessionId);
	}

	// 历史消费记录
	@Override
	public ConsumHistory getConsumHistory(String... params) throws Exception {
		// 到数据库获取
		ConsumHistory history = new ConsumHistory();
		ArrayList<ConsumHistoryItem> resultList = null;
		try {
			resultList = mUserDao.selectConsumHistory();
			if (resultList != null
					&& resultList.size() >= FinalValue.CONSUM_TOTAL_COUNT) {
				history.setResultList(resultList);
			}
		} finally {
			if (resultList == null || resultList.size() == 0
					|| resultList.size() < FinalValue.CONSUM_TOTAL_COUNT) {
				// 从网络获取
				history = mUserNet.getConsumHistoryFromService(getSessionId(),
						params[0], params[1]);
				if (history != null) {
					FinalValue.CONSUM_TOTAL_PAGE = history.getPageNum();
					FinalValue.CONSUM_TOTAL_COUNT = history.getTotalCount();
					// 并写入数据库
					mUserDao.insertConsumHistory(history.getResultList());
				}
			}
		}
		return history;
	}

	// 积分和用户等级
	@Override
	public Level getUserLevel() throws Exception {
		return mUserNet.getLevel(getSessionId());
	}

	// 积分获得记录
	@Override
	public ScoreGetHistory getScoreGetHistory(String... params)
			throws Exception {
		// 到数据库获取
		ScoreGetHistory scoreGetHistory = new ScoreGetHistory();
		ArrayList<ScoreGetHistoryItem> resultList = null;
		try {
			resultList = mUserDao.selectScoreHistory();
			if (resultList != null
					&& resultList.size() >= FinalValue.SCORE_TOTAL_COUNT) {
				scoreGetHistory.setResultList(resultList);
			}
		} finally {
			if (resultList == null || resultList.size() == 0
					|| resultList.size() < FinalValue.SCORE_TOTAL_COUNT) {
				scoreGetHistory = mUserNet.getScoreHistory(getSessionId(),
						params[0], params[1]);
				FinalValue.SCORE_TOTAL_PAGE = scoreGetHistory.getPageNum();
				FinalValue.SCORE_TOTAL_COUNT = scoreGetHistory.getTotalCount();
				// 存入数据库
				mUserDao.insertScoreHistory(scoreGetHistory.getResultList());
			}
		}
		return scoreGetHistory;
	}

	// 积分消费记录
	@Override
	public UseScoreHistory getScoreUseHistory(String... params)
			throws Exception {
		// 到数据库获取
		UseScoreHistory scoreUseHistory = new UseScoreHistory();
		ArrayList<UseScoreHistoryItem> resultList = null;
		try {
			resultList = mUserDao.selectUseScoreHistory();
			if (resultList != null
					&& resultList.size() >= FinalValue.SCORE_USE_TOTAL_COUNT) {
				scoreUseHistory.setResultList(resultList);
			}
		} finally {
			if (resultList == null || resultList.size() == 0
					|| resultList.size() < FinalValue.SCORE_USE_TOTAL_COUNT) {
				scoreUseHistory = mUserNet.getUseScoreHistory(getSessionId(),
						params[0], params[1]);
				if (scoreUseHistory != null) {
					FinalValue.SCORE_USE_TOTAL_PAGE = scoreUseHistory
							.getPageNum();
					FinalValue.SCORE_USE_TOTAL_COUNT = scoreUseHistory
							.getTotalCount();
					// 存入数据库
					mUserDao.insertUseScoreHistory(scoreUseHistory
							.getResultList());
				}
			}
		}
		return scoreUseHistory;
	}

	// 注销
	@Override
	public String toLogout() throws Exception {
		String sessionId = null;
		sessionId = getSessionId();
		removeUserData();
		return mUserNet.toUnRegister(sessionId);
	}

	// 有奖推广
	@Override
	public IntroductionInfo toIntroductionInfo() throws Exception {
		return mUserNet.getIntroductionInfoDetails(getSessionId());
	}

	// 保存用户登陆信息
	@Override
	public void addUserLoginInfo(User user) throws Exception {
		mUserDao.insertUserLoginInfo(user);
	}

	@Override
	public void delteUserLoginInfo() throws Exception {
		mUserDao.deleteUserLoginInfo();
	}

	@Override
	public Boolean UserAdvice(String... params) throws Exception {
		String code = mUserNet.userAdvice(params[0], params[1]);
		if (StrTools.isNull(code)) {
			return false;
		}
		if (code.equals("0")) {
			return true;
		}
		return false;
	}

	// 清除用户的缓存信息
	@Override
	public void removeUserData() throws Exception {
		mUserDao.deleteConsumHistory();
		mUserDao.deleteScoreHistory();
		mUserDao.deleteUseScoreHistory();
		mUserDao.deleteUserLoginInfo();
		mUserDao.delteUser();
		mUserDao.delteUserSessionId();
	}

	// 上次头像
	@Override
	public List<String> upLoadUserImage(String params, String path)
			throws Exception {
		// sessionid
		List<String> result = null;
		String sessionId = null;
		sessionId = getSessionId();
		// 对图片精选base64 编码处理
		String imageFile = ImageTools.getFileToByte(path);
		if (imageFile != null && !StrTools.isNull(params)
				&& !StrTools.isNull(sessionId)) {
			// Log.d("加密后：", params + "@" + imageFile);
			result = mUserNet.upLoadUserImage(sessionId, params + "@"
					+ imageFile);
			if (result.get(0).equals("0")) {
				mUserDao.updateUserInfo(1, "path", result.get(1));
			}
			return result;
		}
		return null;
	}
}
