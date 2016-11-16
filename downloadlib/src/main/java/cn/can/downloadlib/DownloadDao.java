package cn.can.downloadlib;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "download".
*/
public class DownloadDao extends AbstractDao<DownloadDBEntity, String> {

    public static final String TABLENAME = "download";

    /**
     * Properties of entity DownloadDBEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property DownloadId = new Property(0, String.class, "downloadId", true, "DOWNLOAD_ID");
        public final static Property TotalSize = new Property(1, Long.class, "totalSize", false, "TOTAL_SIZE");
        public final static Property DownloadedSize = new Property(2, Long.class, "downloadedSize", false, "DOWNLOADED_SIZE");
        public final static Property Url = new Property(3, String.class, "url", false, "URL");
        public final static Property SaveDirPath = new Property(4, String.class, "saveDirPath", false, "SAVE_DIR_PATH");
        public final static Property FileName = new Property(5, String.class, "fileName", false, "FILE_NAME");
        public final static Property DownloadStatus = new Property(6, Integer.class, "downloadStatus", false, "DOWNLOAD_STATUS");
        public final static Property Icon = new Property(7, String.class, "icon", false, "ICON");
    };


    public DownloadDao(DaoConfig config) {
        super(config);
    }
    
    public DownloadDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"download\" (" + //
                "\"DOWNLOAD_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: downloadId
                "\"TOTAL_SIZE\" INTEGER," + // 1: totalSize
                "\"DOWNLOADED_SIZE\" INTEGER," + // 2: downloadedSize
                "\"URL\" TEXT," + // 3: url
                "\"SAVE_DIR_PATH\" TEXT," + // 4: saveDirPath
                "\"FILE_NAME\" TEXT," + // 5: fileName
                "\"DOWNLOAD_STATUS\" INTEGER," + // 6: downloadStatus
                "\"ICON\" TEXT);"); // 7: icon
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"download\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DownloadDBEntity entity) {
        stmt.clearBindings();
 
        String downloadId = entity.getDownloadId();
        if (downloadId != null) {
            stmt.bindString(1, downloadId);
        }
 
        Long totalSize = entity.getTotalSize();
        if (totalSize != null) {
            stmt.bindLong(2, totalSize);
        }
 
        Long downloadedSize = entity.getDownloadedSize();
        if (downloadedSize != null) {
            stmt.bindLong(3, downloadedSize);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(4, url);
        }
 
        String saveDirPath = entity.getSaveDirPath();
        if (saveDirPath != null) {
            stmt.bindString(5, saveDirPath);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(6, fileName);
        }
 
        Integer downloadStatus = entity.getDownloadStatus();
        if (downloadStatus != null) {
            stmt.bindLong(7, downloadStatus);
        }
 
        String icon = entity.getIcon();
        if (icon != null) {
            stmt.bindString(8, icon);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DownloadDBEntity entity) {
        stmt.clearBindings();
 
        String downloadId = entity.getDownloadId();
        if (downloadId != null) {
            stmt.bindString(1, downloadId);
        }
 
        Long totalSize = entity.getTotalSize();
        if (totalSize != null) {
            stmt.bindLong(2, totalSize);
        }
 
        Long downloadedSize = entity.getDownloadedSize();
        if (downloadedSize != null) {
            stmt.bindLong(3, downloadedSize);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(4, url);
        }
 
        String saveDirPath = entity.getSaveDirPath();
        if (saveDirPath != null) {
            stmt.bindString(5, saveDirPath);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(6, fileName);
        }
 
        Integer downloadStatus = entity.getDownloadStatus();
        if (downloadStatus != null) {
            stmt.bindLong(7, downloadStatus);
        }
 
        String icon = entity.getIcon();
        if (icon != null) {
            stmt.bindString(8, icon);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public DownloadDBEntity readEntity(Cursor cursor, int offset) {
        DownloadDBEntity entity = new DownloadDBEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // downloadId
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // totalSize
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // downloadedSize
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // url
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // saveDirPath
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // fileName
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // downloadStatus
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // icon
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DownloadDBEntity entity, int offset) {
        entity.setDownloadId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTotalSize(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setDownloadedSize(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setUrl(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSaveDirPath(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setFileName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDownloadStatus(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setIcon(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected final String updateKeyAfterInsert(DownloadDBEntity entity, long rowId) {
        return entity.getDownloadId();
    }
    
    @Override
    public String getKey(DownloadDBEntity entity) {
        if(entity != null) {
            return entity.getDownloadId();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
