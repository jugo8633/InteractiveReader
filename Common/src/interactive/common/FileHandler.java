package interactive.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.SparseArray;

public class FileHandler
{
	public final String		ENCODING		= "UTF-8";
	private final String	FILE_RESOURCE	= "resource";

	String[]				astrBookPath	= { "/sdcard/download/", "/sdcard/Download/", "/mnt/sdcard/download/",
			"/sdcard/external_sd/", "/emmc/", "/mnt/sdcard/external_sd/", "/mnt/external_sd/", "/sdcard/sd/",
			"/mnt/sdcard/bpemmctest/", "/mnt/sdcard/_ExternalSD/", "/mnt/sdcard-ext/", "/mnt/Removable/MicroSD/",
			"/Removable/MicroSD/", "/mnt/external1/", "/mnt/extSdCard/", "/mnt/extsd/", "/mnt/usb_storage/",
			"/mnt/extSdCard/", "/mnt/UsbDriveA/", "/mnt/UsbDriveB/" };

	public FileHandler()
	{
		super();
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	public boolean unExpressFile(final String strSrcPath, final String strDistPath)
	{
		File expressFile = new File(strSrcPath);
		if (expressFile.exists())
		{
			try
			{
				Logs.showTrace("Unexpress file: " + expressFile.toString());
				Zip.unzip(expressFile.toString(), strDistPath);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Logs.showTrace("Unexpress file fail error: " + e.getMessage());
				return false;
			}
			return true;
		}
		Logs.showTrace("Unexpress file fail error: file is not exist: " + expressFile);
		return false;
	}

	public void initPath(Activity activity)
	{
		//		mstrPreviewPath = activity.getExternalFilesDir(null).getPath() + File.separator + "PreBook" + File.separator;
		//		mstrBookshelfsPath = activity.getExternalFilesDir(null).getPath() + File.separator + "Bookshelfs"
		//				+ File.separator;
		//		createPath(mstrPreviewPath);
		//		createPath(mstrBookshelfsPath);
		createPath(Type.DEFAULT_STORAGE);
	}

	private void createPath(final String strPath)
	{
		File f = new File(strPath);
		if (!f.isDirectory())
		{
			f.mkdirs();
		}
		f = null;
	}

	public File[] getPreviewBookList(String strPath)
	{
		ArrayList<File> inFiles = new ArrayList<File>();
		File[] a = new File(strPath).listFiles();
		for (File file : a)
		{
			if (file.isDirectory())
			{
				inFiles.add(file);
			}
		}
		a = new File[inFiles.size()];
		for (int i = 0; i < inFiles.size(); i++)
		{
			a[i] = inFiles.get(i);
			Logs.showTrace("get preview book: " + a[i].toString());
		}

		Arrays.sort(a, new Comparator<File>()
		{
			public int compare(File f1, File f2)
			{
				return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			}
		});

		return a;
	}

	public String getStringFromFile(String filePath) throws Exception
	{
		File fl = new File(filePath);
		FileInputStream fin = new FileInputStream(fl);
		String ret = convertStreamToString(fin);
		fin.close();
		return ret;
	}

	public String convertStreamToString(InputStream is) throws Exception
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

	public boolean checkAssetFile(Activity activity, String strFile)
	{
		if (null == strFile)
		{
			return false;
		}

		try
		{
			return Arrays.asList(activity.getResources().getAssets().list("")).contains(strFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public void copyFileFromAssets(Activity activity, String file, String dest) throws Exception
	{
		InputStream in = null;
		OutputStream fout = null;
		int count = 0;

		try
		{
			in = activity.getAssets().open(file);
			fout = new FileOutputStream(new File(dest));

			byte data[] = new byte[1024];
			while ((count = in.read(data, 0, 1024)) != -1)
			{
				fout.write(data, 0, count);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
				}
			}
			if (fout != null)
			{
				try
				{
					fout.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}

	public int getBookInfo(Activity activity, SparseArray<String> saResInfo)
	{
		if (null == saResInfo)
		{
			return 0;
		}

		String strInfo = null;

		try
		{
			InputStreamReader inputStream = new InputStreamReader(activity.getAssets().open(FILE_RESOURCE), ENCODING);
			BufferedReader bufReader = new BufferedReader(inputStream);

			while ((strInfo = bufReader.readLine()) != null)
			{
				saResInfo.put(saResInfo.size(), strInfo);
			}

			if (null != bufReader)
			{
				bufReader.close();
				bufReader = null;
			}

			if (null != inputStream)
			{
				inputStream.close();
				inputStream = null;
			}
		}
		catch (Exception e)
		{
			Logs.showTrace(e.getMessage());
			strInfo = null;
		}

		return saResInfo.size();
	}

	public boolean UnExpressBook(String strPath, String strBookName, String strBookFormat)
	{
		if (null != strPath)
		{
			return unExpressFile(strPath + strBookName + "." + strBookFormat, strPath + strBookName);
		}
		strPath = null;
		return false;
	}

	private String searchBookFile(String strBookFolder, String strFindFile)
	{
		File fstream = null;
		String strFilePath = null;
		String strFile = null;
		String strSdPath = null;

		strSdPath = getSdcardPath();
		Logs.showTrace("get sd path : " + strSdPath);
		if (null != strSdPath)
		{
			strFilePath = searchFilePath(strSdPath + "Download" + File.separator + strBookFolder, strFindFile);
			if (null != strFilePath)
			{
				return strFilePath;
			}
		}

		for (String strPath : astrBookPath)
		{
			strFile = strPath + strBookFolder + File.separator + strFindFile;
			fstream = new File(strFile);
			if (null != fstream)
			{
				if (fstream.exists())
				{
					strFilePath = strPath;
					break;
				}
				fstream = null;
			}
		}

		return strFilePath;
	}

	public String getSdcardPath()
	{
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();
			return sdDir.toString() + File.separator;
		}

		return null;
	}

	public String searchFilePath(String path, String find)
	{
		Logs.showTrace("search file path=" + path + " find=" + find);
		try
		{
			File name = new File(path);
			String[] directory = name.list();

			for (String f : directory)
			{
				File check = new File(name.getPath() + File.separator + f);

				if (check.isFile() && f.equals(find))
				{
					return (name.getPath() + File.separator);
				}
				else if (check.isDirectory())
				{
					searchFilePath(name.getPath() + File.separator + f, find);
				}
			}
		}
		catch (Exception e)
		{
			Logs.showTrace("search file error " + e.getMessage());
		}

		return null;
	}

	public String getExistBookPath(String strBookPath)
	{
		return searchBookFile(strBookPath + File.separator, "config.xml");
	}

	public String getObbFile(String strPath, String strPackName)
	{
		File fstream = new File(strPath);
		String[] listFile = fstream.list();
		if (null == listFile)
		{
			return null;
		}
		for (String f : listFile)
		{
			if (f.contains(strPackName))
			{
				return f;
			}
		}
		return null;
	}

	public boolean fileRename(String strOldPath, String strOldFile, String strNewPath, String strNewFile)
	{
		String strOld = strOldPath + strOldFile;
		File d2 = new File(strOld);
		Logs.showTrace("Move file:" + strOld + " to:" + strNewPath + "/" + strNewFile);
		return d2.renameTo(new File(strNewPath, strNewFile));
	}

	public boolean deleteFile(String strFile)
	{
		// A File object to represent the filename
		File f = new File(strFile);

		// Make sure the file or directory exists and isn't write protected
		if (!f.exists())
		{
			throw new IllegalArgumentException("Delete: no such file or directory: " + strFile);
		}

		if (!f.canWrite())
		{
			throw new IllegalArgumentException("Delete: write protected: " + strFile);
		}

		// If it is a directory, make sure it is empty
		if (f.isDirectory())
		{
			String[] files = f.list();
			if (files.length > 0)
				throw new IllegalArgumentException("Delete: directory not empty: " + strFile);
		}

		// Attempt to delete it
		return f.delete();
	}

	public boolean delete(String strFile)
	{
		Logs.showTrace("delete file:" + strFile);
		File file = new File(strFile);

		File[] flist = null;

		if (file.isFile())
		{
			return file.delete();
		}

		if (!file.isDirectory())
		{
			return false;
		}

		flist = file.listFiles();
		if (flist != null && flist.length > 0)
		{
			for (File f : flist)
			{
				if (!delete(f.toString()))
				{
					return false;
				}
			}
		}

		return file.delete();
	}

	public static StringBuffer getFileContent(String path) throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(path));
		String inputLine;
		StringBuffer temp = new StringBuffer();
		while ((inputLine = in.readLine()) != null)
			temp.append(inputLine + "\n");

		in.close();
		in = null;
		inputLine = null;
		return temp;
	}

	synchronized public static boolean isFileExist(String strFilePath)
	{
		File fstream = null;
		fstream = new File(strFilePath);
		if (null == fstream || !fstream.exists() || !fstream.isFile())
		{
			return false;
		}
		fstream = null;
		return true;
	}

	public boolean checkPath(String strPath, boolean bCreate)
	{
		File fstream = new File(strPath);
		if (null != fstream && fstream.exists())
		{
			return true;
		}

		if (bCreate)
		{
			fstream.mkdir();
			if (fstream.exists())
			{
				return true;
			}
		}

		return false;
	}
}
