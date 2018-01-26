#!/usr/bin/env python
# -*- encoding: utf-8 -*-
# Created on 2018-01-24 11:44:09
# Project: v2ex

from pyspider.libs.base_handler import *
import re
import os, sys
import MySQLdb
import random


class Handler(BaseHandler):
    crawl_config = {
    }


    def __init__(self):
        self.db = MySQLdb.connect('127.0.0.1', 'root', '123456', 'toutiao', charset='utf8');

    # 添加咨询到数据库
    def addQuestion(self, title, link, image, comment_count):
        user_id = random.randint(1, 10)
        try:
            cursor = self.db.cursor()
            sql = 'insert into news(title, link ,image, like_count,comment_count,created_date,user_id) values ("%s","%s","%s",0, %d,now(),%d)' % (
            title, link, image, comment_count, user_id);
            cursor.execute(sql)
            self.db.commit()
            return int(cursor.lastrowid)
        except Exception as e:
            print e
            self.db.rollback()

    # 添加评论到数据库
    def addComments(self, content, entity_id):
        user_id = random.randint(1, 10)
        try:
            cursor = self.db.cursor()
            sql = 'insert into comment(content, user_id ,entity_id,entity_type,created_date,status) values ("%s",%d,%d,0,now(),0)' % (
            content, user_id, entity_id,);
            cursor.execute(sql)
            self.db.commit();

        except Exception as e:
            print e
            self.db.rollback()

    # 开始
    @every(minutes=24 * 60)
    def on_start(self):
        self.crawl('https://www.v2ex.com/', callback=self.index_page)

    # 首页爬栏目标签
    @config(age=10 * 24 * 60 * 60)
    def index_page(self, response):
        for each in response.doc('a[href^="https://www.v2ex.com/?tab="]').items():
            self.crawl(each.attr.href, callback=self.tab_page)

    # 标签页爬板块标签
    @config(age=10 * 24 * 60 * 60)
    def tab_page(self, response):
        for each in response.doc('div.cell>a[href^="https://www.v2ex.com/go/"]').items():
            self.crawl(each.attr.href, callback=self.board_page)

    # 板块页爬问题标签
    @config(age=10 * 24 * 60 * 60)
    def board_page(self, response):
        for each in response.doc('span.item_title>a').items():
            href = each.attr.href
            self.crawl(href[0:href.find('#')], callback=self.detail_page)

        for each in response.doc('a.page_normal').items():
            href = each.attr.href
            self.crawl(each.attr.href, callback=self.board_page)

    # 问题页爬内容
    @config(priority=2)
    def detail_page(self, response):
        list = []  ## 空列表
        comments_count = 0

        comments = response.doc('div.reply_content').items()
        if comments == None:
            print '评论为空'
        else:
            # 正则匹配评论数
            r = re.compile('[\d]+')
            str = response.doc('div.cell>span.gray').text()
            se = re.search(r, str)
            if se == None:
                pass
            else:
                comments_count = se.group()

            for item in comments:
                list.append(item.text())  ## 使用 append() 添加元素

        print os.getcwd()

        link = response.url
        title = response.doc('h1').text()
        img = response.doc('div.fr img').attr('src')
        news_id= self.addQuestion(title, link, img, int(comments_count))
        for item in list:
            self.addComments(item.replace('"', '\\"'), news_id)

        return {
            "url": link,
            "title": title,
            "content": response.doc('div.topic_content').text(),
            "comments": list,
            "comments_count": comments_count,
            "img": img
        }

