package com.invengo.warehouse.service.impl;

import com.invengo.warehouse.dao.impl.DbWeb;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 服务器接口
 * Created by LZR on 2017/8/15.
 */

public class WareHouseSrv extends HttpServlet {
	private DbWeb db = new DbWeb();	// 数据库

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println("Hello World!");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.setContentType("text/html;charset=utf-8");
		res.setHeader("Access-Control-Allow-Origin", "*");

		PrintWriter out = res.getWriter();
		String srvNam = req.getParameter("srv");
		StringBuilder r = new StringBuilder();
		r.append("{\"ok\":");

		if (srvNam != null && db.open()) {
			switch (srvNam) {
				// 基础
				case "signin":
					r.append(db.signin(req.getParameter("username"), req.getParameter("password")));
					break;
				case "getDevInfo":
					r.append(db.getDevInfo(req.getParameter("devid")));
					break;
				case "getBorrowByDev":
					r.append(db.getBorrowByDev(req.getParameter("devid")));
					break;

				// 出库
				case "getAllOut":	// 用于测试
					r.append(db.getAllOut());
					break;
				case "getAllBorrow":	// 用于测试
					r.append(db.getAllBorrow());
					break;
				case "crtOut":
					r.append(db.crtOut(req.getParameter("rid")));
					break;
				case "crtBorrow":
					r.append(db.crtBorrow(req.getParameter("rid"), req.getParameter("devid")));
					break;
				case "getAllUdOut":
					r.append(db.getAllUdOut());
					break;
				case "getOutInfo":
					r.append(db.getOutInfo(req.getParameter("rid")));
					break;
				case "getDevsByOut":
					r.append(db.getDevsByOut(req.getParameter("rid")));
					break;
				case "delOneBorrow":
					r.append(db.delOneBorrow(req.getParameter("rid"), req.getParameter("devid")));
					break;
				case "delOut":
					r.append(db.delOut(req.getParameter("rid")));
					break;
				case "outOk":
					r.append(db.outOk(req.getParameter("rid"), req.getParameter("user")));
					break;

				// 盘点
				case "getAllPlan":	// 用于测试
					r.append(db.getAllPlan());
					break;
				case "getAllRecord":	// 用于测试
					r.append(db.getAllRecord());
					break;
				case "crtPlan":
					r.append(db.crtPlan(req.getParameter("rid"), req.getParameter("user")));
					break;
				case "crtRecord":
					r.append(db.crtRecord(req.getParameter("rid"), req.getParameter("devid"), req.getParameter("user"), req.getParameter("status")));
					break;
				case "getAllUdPlan":
					r.append(db.getAllUdPlan());
					break;
				case "getPlanInfo":
					r.append(db.getPlanInfo(req.getParameter("rid")));
					break;
				case "getDevsByPlan":
					r.append(db.getDevsByPlan(req.getParameter("rid")));
					break;
				case "delOneRecord":
					r.append(db.delOneRecord(req.getParameter("rid"), req.getParameter("devid")));
					break;
				case "delPlan":
					r.append(db.delPlan(req.getParameter("rid")));
					break;
				case "clearRecord":
					r.append(db.clearRecord(req.getParameter("rid"), req.getParameter("devid")));
					break;
				case "markRecord":
					r.append(db.markRecord(req.getParameter("rid"), req.getParameter("devid"), req.getParameter("user")));
					break;
				case "planOk":
					r.append(db.planOk(req.getParameter("rid")));
					break;

				// 其它
				default:
					r.append(false);
					break;
			}
			db.close();
		} else {
			r.append(false);
		}

		r.append('}');
		out.println(r.toString());
	}

}
