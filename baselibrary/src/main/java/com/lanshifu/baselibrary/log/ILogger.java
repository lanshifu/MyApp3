package com.lanshifu.baselibrary.log;

public interface ILogger {
    public void info(String msg);

    public void debug(String msg);

    public void warn(String msg);

    public void error(String msg);

    public void error(String msg, Throwable t);
}