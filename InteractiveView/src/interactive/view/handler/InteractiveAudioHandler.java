package interactive.view.handler;

import interactive.view.audio.AudioPlayer;
import android.util.SparseArray;

public class InteractiveAudioHandler
{

	private SparseArray<AudioPlayer>	listAudioPlay	= null;

	public InteractiveAudioHandler()
	{
		super();
		listAudioPlay = new SparseArray<AudioPlayer>();
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	public void addAudioPlayer(AudioPlayer audioPlayer)
	{
		listAudioPlay.put(listAudioPlay.size(), audioPlayer);
	}

	public void releaseAllAudio()
	{
		for (int i = 0; i < listAudioPlay.size(); ++i)
		{
			listAudioPlay.get(i).release();
		}
	}

}
