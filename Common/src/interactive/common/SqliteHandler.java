package interactive.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

public class SqliteHandler extends SQLiteOpenHelper
{

	public static final String	TABLE_NAME_FAVORITE	= "favorite";
	public static final String	COLUMN_CHAPTER		= "chapter";
	public static final String	COLUMN_PAGE			= "page";
	private final static String	DATABASE_NAME		= "appcross.db";
	private final static int	DATABASE_VERSION	= 1;

	public class FavoriteData
	{
		public int	mnChapter	= Type.INVALID;
		public int	mnPage		= Type.INVALID;

		public FavoriteData(int nChapter, int nPage)
		{
			mnChapter = nChapter;
			mnPage = nPage;
		}
	}

	public SqliteHandler(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public SqliteHandler(Context context, String name, CursorFactory factory, int version)
	{
		super(context, name, factory, version);
	}

	public SqliteHandler(Context context, String name, CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler)
	{
		super(context, name, factory, version, errorHandler);
	}

	/**
	 * 如果資料庫不存在 則呼叫onCreate
	 */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		final String DATABASE_CREATE_TABLE = "create table " + TABLE_NAME_FAVORITE + "("
				+ "_ID INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL," + "chapter INT," + "page INT" + ")";
		try
		{
			db.execSQL(DATABASE_CREATE_TABLE);
			Logs.showTrace("Create Database Success!!");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * 版本更新時被呼叫 oldVersion=舊的資料庫版本；newVersion=新的資料庫版本
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS favorite"); //刪除舊有的資料表
		onCreate(db);
	}

	/**
	 * 每次成功打開數據庫後首先被執行     
	 */
	@Override
	public void onOpen(SQLiteDatabase db)
	{
		super.onOpen(db);
	}

	@Override
	public synchronized void close()
	{
		super.close();
	}

	public void addFavorite(int nChapter, int nPage)
	{
		long nRet = Type.INVALID;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_CHAPTER, nChapter);
		values.put(COLUMN_PAGE, nPage);
		nRet = db.insert(TABLE_NAME_FAVORITE, null, values);
		if (-1 == nRet)
		{
			Logs.showTrace("Add Database Data Fail!!");
		}
	}

	public void deleteFavorite(int nChapter, int nPage)
	{
		int nRet = Type.INVALID;
		SQLiteDatabase db = this.getWritableDatabase();
		nRet = db.delete(TABLE_NAME_FAVORITE, "chapter = " + nChapter + " and page = " + nPage, null);
		if (0 == nRet)
		{
			Logs.showTrace("Delete Database Data Fail!!");
		}
	}

	public void updateFavorite(int nOldChapter, int nOldPage, int nNewChapter, int nNewPage)
	{
		int nRet = Type.INVALID;
		ContentValues values = new ContentValues();
		values.put(COLUMN_CHAPTER, nNewChapter);
		values.put(COLUMN_PAGE, nNewPage);

		SQLiteDatabase db = this.getWritableDatabase();
		nRet = db.update(TABLE_NAME_FAVORITE, values, COLUMN_CHAPTER + " = " + nOldChapter + " and " + COLUMN_PAGE
				+ " = " + nOldPage, null);
		if (0 >= nRet)
		{
			Logs.showTrace("Update Database Fail!!");
		}
	}

	public void getFavoriteData(SparseArray<FavoriteData> listData)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		//		String[] columns = { _ID, COLUMN_CHAPTER, COLUMN_PAGE };
		//		Cursor cursor = db.query(TABLE_NAME_FAVORITE, columns, null, null, null, null, null);
		//		activity.startManagingCursor(cursor);

		Cursor cursor = db.rawQuery("SELECT " + COLUMN_CHAPTER + " , " + COLUMN_PAGE + " FROM " + TABLE_NAME_FAVORITE
				+ " ORDER BY _ID DESC", null);
		int rows_num = cursor.getCount();//取得資料表列數
		if (rows_num != 0)
		{
			FavoriteData favoriteData = null;
			cursor.moveToFirst(); //將指標移至第一筆資料
			for (int i = 0; i < rows_num; ++i)
			{
				favoriteData = new FavoriteData(cursor.getInt(0), cursor.getInt(1));
				listData.put(listData.size(), favoriteData);
				favoriteData = null;
				cursor.moveToNext();//將指標移至下一筆資料
			}
		}
		cursor.close(); //關閉Cursor
	}
}
