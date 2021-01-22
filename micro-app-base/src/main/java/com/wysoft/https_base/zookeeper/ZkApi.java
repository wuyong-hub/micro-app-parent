package com.wysoft.https_base.zookeeper;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZkApi {
	@Autowired
	private Logger logger;
	@Autowired
	private ZooKeeper zkClient;

	/**
	 * 判断指定节点是否存在.
	 * 
	 * @param path 节点路径
	 * @param needWatch 指定是否复用zookeeper中默认的Watcher
	 * @return boolean
	 */
	public boolean exists(String path, boolean needWatch) {
		try {
			Stat stat = zkClient.exists(path, needWatch);
			return stat != null ? true : false;
		} catch (Exception e) {
			logger.error("【断指定节点是否存在异常】{},{}", path, e);
			return false;
		}
	}

	/**
	 * 检测结点是否存在 并设置监听事件 三种监听类型： 创建，删除，更新.
	 *
	 * @param path 节点路径
	 * @param watcher 传入指定的监听类
	 * @return boolean
	 */
	public boolean exists(String path, Watcher watcher) {
		try {
			Stat stat = zkClient.exists(path, watcher);
			return stat != null ? true : false;
		} catch (Exception e) {
			logger.error("【断指定节点是否存在异常】{},{}", path, e);
			return false;
		}
	}

	/**
	 * 创建节点.
	 * 
	 * @param path 节点路径
	 * @param data 数据
	 * @param createMode 创建模式
	 * @return boolean
	 */
	public boolean createNode(String path, String data,CreateMode createMode) {
		try {
			zkClient.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
			return true;
		} catch (Exception e) {
			logger.error("【创建节点异常】{},{},{}", path, data, e);
			return false;
		}
	}

	/**
	 * 修改节点.
	 * 
	 * @param path 节点路径
	 * @param data 数据
	 * @return boolean
	 */
	public boolean updateNode(String path, String data) {
		try {
			// zk的数据版本是从0开始计数的。如果客户端传入的是-1，则表示zk服务器需要基于最新的数据进行更新。如果对zk的数据节点的更新操作没有原子性要求则可以使用-1.
			// version参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败. 指定version为-1则忽略版本检查
			zkClient.setData(path, data.getBytes(), -1);
			return true;
		} catch (Exception e) {
			logger.error("【修改节点异常】{},{},{}", path, data, e);
			return false;
		}
	}

	/**
	 * 删除节点.
	 * 
	 * @param path 节点路径
	 * @return boolean
	 */
	public boolean deleteNode(String path) {
		try {
			// version参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败. 指定version为-1则忽略版本检查
			zkClient.delete(path, -1);
			return true;
		} catch (Exception e) {
			logger.error("【删除节点异常】{},{}", path, e);
			return false;
		}
	}

	/**
	 * 获取当前节点的子节点(不包含孙子节点).
	 * @param path 路径
	 * @return List
	 * @throws KeeperException 异常
	 * @throws InterruptedException 异常
	 */
	public List<String> getChildren(String path) throws KeeperException, InterruptedException {
		List<String> list = zkClient.getChildren(path, false);
		return list;
	}

	/**
	 * 获取指定节点的值.
	 * 
	 * @param path 节点路径
	 * @param watcher watcher
	 * @return String 
	 */
	public String getData(String path, Watcher watcher) {
		try {
			Stat stat = new Stat();
			byte[] bytes = zkClient.getData(path, watcher, stat);
			return new String(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
