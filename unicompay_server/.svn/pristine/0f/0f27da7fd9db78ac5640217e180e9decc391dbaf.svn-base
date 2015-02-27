package org.marker.protocol.sgip;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.marker.protocol.exception.BindException;
import org.marker.protocol.exception.ConnectionException;
import org.marker.protocol.sgip.conn.Connection;
import org.marker.protocol.sgip.msg.Bind;
import org.marker.protocol.sgip.msg.BindResp;
import org.marker.protocol.sgip.msg.Deliver;
import org.marker.protocol.sgip.msg.Message;
import org.marker.protocol.sgip.msg.Report;
import org.marker.protocol.sgip.msg.Submit;
import org.marker.protocol.sgip.msg.Unbind;
import org.marker.protocol.sgip.thread.ListenThread;

public abstract class Session {
    private Connection conn;
    private boolean connected = false;
    private boolean bound = false;
    private ListenThread listenThread = null;

    private long freeTime = 30000L;
    private String localUser = null;
    private String localPass = null;
    private int localPort = 0;
    private Bind bind_bak;
    private long nodeId;
    private transient int count = 0;
    Timer timer;

    public Session(Connection conn) {
        this.conn = conn;
    }

    public Session(String localUser, String localPass, int localPort) {
        this.localUser = localUser;
        this.localPass = localPass;
        this.localPort = localPort;
        try {
//            System.out.println("before listenThread");
            new ListenThread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected void log(int level, String msg, Throwable t) {
        System.out.println(new SimpleDateFormat("hh-mm-ss").format(new Date()) + "->" + msg);
        if (t != null)
            t.printStackTrace();
    }

    public BindResp open(Bind bind) throws BindException, IOException {
//        if (this.count == 3) {
//            throw new RuntimeException("maxCount init SGIP error......");
//        }
        startTimer();
        if (this.bind_bak != null)
            closeNotException();
        if (this.bind_bak != bind)
            this.bind_bak = bind;
        if (this.listenThread == null && localPort > 0) {
            this.listenThread = new ListenThread(this);
            this.listenThread.start();
        }
        try {
            this.conn.open();
            this.connected = true;
            this.conn.send(bind);
            BindResp resp = (BindResp) this.conn.recv();
            if (resp.getResult() == 0)
                this.bound = true;
            else
                throw new BindException("login fiald status:" + resp.getResult());
            return resp;
        } catch (BindException exception) {
            throw exception;
        } catch (Exception e) {
            throw new RuntimeException("login SGIP error......"+ e.getMessage());
        }
    }

    public Message sendSubmit(Submit msg) throws Exception {
        startTimer();
        if ((!isConnected()) || (!isBound()))
            open(this.bind_bak);
        try {
            this.conn.send(msg);
            return this.conn.recv();
        } catch (Exception e) {
            this.connected = false;
            this.bound = false;
//            Thread.sleep(1000L);
            throw new RuntimeException(e.getMessage(),e);
        }
//        return sendSubmit(msg);
    }

    public void startTimer() {
        if (this.timer != null)
            this.timer.cancel();
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            public void run() {
                Session.this.closeNotException();
            }
        }, this.freeTime);
    }

    public void closeNotException() {
        try {
            close();
        } catch (Exception localException) {
        } finally {
            this.timer = null;
        }
    }

    public void close() throws ConnectionException {
        this.connected = false;
        this.bound = false;
        try {
            this.conn.send(new Unbind());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void onMessage(Deliver paramDeliver);

    public abstract void onReport(Report paramReport);

    public abstract void onTerminate();

    public boolean isBound() {
        return this.bound;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public String getLocalPass() {
        return this.localPass;
    }

    public void setLocalPass(String localPass) {
        this.localPass = localPass;
    }

    public int getLocalPort() {
        return this.localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public String getLocalUser() {
        return this.localUser;
    }

    public void setLocalUser(String localUser) {
        this.localUser = localUser;
    }

    public long getFreeTime() {
        return this.freeTime;
    }

    public void setFreeTime(long freeTime) {
        this.freeTime = freeTime;
    }

    public long getNodeId() {
        return this.nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }
}