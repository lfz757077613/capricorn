package cn.laifuzhi.capricorn.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 从log4j里抄过来的
 * Check every now and then that a certain file has not changed. If it
 * has, then call the {@link #doOnChange} method.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since version 0.9.1
 */
@Slf4j
public abstract class FileWatchdog extends Thread {

    /**
     * The default delay between every file modification check, set to 60
     * seconds.
     */
    private static final long DEFAULT_DELAY = 60000;
    /**
     * The path of the file to observe for changes.
     */
    private String filepath;

    /**
     * The delay to observe between every check. By default set {@link
     * #DEFAULT_DELAY}.
     */
    private long delay = DEFAULT_DELAY;

    private File file;
    private long lastModif = 0;
    private boolean warnedAlready = false;
    private boolean interrupted = false;

    protected FileWatchdog(String filename) {
        super("FileWatchdog");
        String filePath = getClass().getResource(File.separator).getPath() + filename;
        this.filepath = filePath;
        file = new File(filePath);
        setDaemon(true);
        checkAndConfigure();
    }

    /**
     * Set the delay to observe between each check of the file changes.
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    protected abstract void doOnChange();

    private void checkAndConfigure() {
        boolean fileExists;
        try {
            fileExists = file.exists();
        } catch (SecurityException e) {
            log.warn("Was not allowed to read check file existance, file:[" +
                    filepath + "].");
            interrupted = true; // there is no point in continuing
            return;
        }

        if (fileExists) {
            long l = file.lastModified(); // this can also throw a SecurityException
            if (l > lastModif) {           // however, if we reached this point this
                lastModif = l;              // is very unlikely.
                doOnChange();
                warnedAlready = false;
            }
        } else {
            if (!warnedAlready) {
                log.debug("[" + filepath + "] does not exist.");
                warnedAlready = true;
            }
        }
    }

    public void run() {
        while (!interrupted) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // no interruption expected
            }
            checkAndConfigure();
        }
    }

    public File getFile() {
        return file;
    }
}
