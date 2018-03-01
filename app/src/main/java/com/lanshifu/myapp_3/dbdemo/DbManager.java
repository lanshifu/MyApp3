package com.lanshifu.myapp_3.dbdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by lanxiaobin on 2018/1/30.
 */

public class DbManager {

    private static final String TAG = "lxb";
    private static Context mContext;

    private static final String DB_NAME = "gtgj_sdk.db";

    private static final String TABLENAME_GROUP_TEMPLATE = "group_template";
    private static final String CREATE_TABLE_GROUP_TEMPLATE = "CREATE TABLE " + TABLENAME_GROUP_TEMPLATE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "count INTEGER," +
            "member TEXT," +
            "name TEXT," +
            "thread_id TEXT,)";


    private static final String TABLENAME_MESSAGE_TEMPLATE = "message_template";
    private static final String CREATE_TABLE_MESSAGE_TEMPLATE = "CREATE TABLE " + TABLENAME_MESSAGE_TEMPLATE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "temp_id INTEGER," +
            "body TEXT," +
            "img_url TEXT," +
            "small_img_url TEXT," +
            "color TEXT ,)";


    /**
     * 入口
     *
     * @param context
     */
    public static void init(Context context) {
        mContext = context;
        initDatabase();
    }

    public static void initDatabase() {
        /**
         * 创建数据库
         * 参数一：数据库名
         * 参数二：模式，一般为MOE_PRIVATE
         * 参数三：游标工厂对象，一般写null，表示系统自动提供
         */
        SQLiteDatabase db = mContext.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        db.execSQL(CREATE_TABLE_GROUP_TEMPLATE);
        db.execSQL(CREATE_TABLE_MESSAGE_TEMPLATE);
        db.close();
    }

    public void insertMessageTemp(GroupTemplateDB data) {

        SQLiteDatabase db = mContext.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        ContentValues values = new ContentValues();
        values.put("name", data.getName());
        values.put("count", data.getCount());
        values.put("member", data.getMember());
        values.put("thread_id", data.getThread_id());
        /**
         *插入数据
         * 参数一：要插入的表名
         * 参数二：要插入的空数据所在的行数，第三个参数部位空，则此参数为null
         * 参数三：要插入的数据
         */
        db.insert(TABLENAME_MESSAGE_TEMPLATE, null, values);
        db.close();
    }

    public void updateData() {
        SQLiteDatabase db = mContext.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        ContentValues values = new ContentValues();
        values.put("name", "赵四");
        values.put("age", 43);
        /**
         * 数据的更新
         * 参数一：要更新的数据所在的表名
         * 参数二：新的数据
         * 参数三：要更新数据的查找条件
         * 参数四：条件的参数
         */
        db.update(TABLENAME_GROUP_TEMPLATE, values, "_id=？", new String[]{"2"});
        db.close();
    }

    public void queryData() {
        SQLiteDatabase db = mContext.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        //查询部分数据
        Cursor cursor = db.rawQuery("select * from " + TABLENAME_GROUP_TEMPLATE + "where name=?", new String[]{"张三"});
        //查询全部数据
        Cursor cursor1 = db.rawQuery("select * from " + TABLENAME_GROUP_TEMPLATE, null);
        //将游标移到第一行
        cursor1.moveToFirst();
        //循环读取数据
        while (!cursor1.isAfterLast()) {
            //获得当前行的标签
            int nameIndex = cursor1.getColumnIndex("name");
            //获得对应的数据
            String name = cursor1.getString(nameIndex);
            int ageIndex = cursor1.getColumnIndex("age");
            int age = cursor1.getInt(ageIndex);
            Log.d(TAG, "name:" + name + "age: " + age);
            //游标移到下一行
            cursor1.moveToNext();
        }
        db.close();
    }
}
