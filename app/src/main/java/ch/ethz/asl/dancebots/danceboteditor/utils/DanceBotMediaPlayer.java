package ch.ethz.asl.dancebots.danceboteditor.utils;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.handlers.AutomaticScrollHandler;
import ch.ethz.asl.dancebots.danceboteditor.listener.MediaPlayerScrollListener;

/**
 * Created by andrin on 21.10.15.
 */
public class DanceBotMediaPlayer implements View.OnClickListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, MediaPlayerScrollListener {

    private static final String LOG_TAG = "DANCE_BOT_MEDIA_PLAYER";

    private final Activity mActivity;
    private TextView mSeekBarTotalTimeView;
    private TextView mSeekBarCurrentTimeView;
    private SeekBar mSeekBar;
    private MediaPlayer mMediaPlayer;
    private boolean mIsReady = false;
    private boolean mIsPlaying = false;
    private int mTotalTime;
    private DanceBotMusicFile mMusicFile;
    private Button mPlayPauseButton;
    private AudioTrack audioTrack;

    public DanceBotMediaPlayer(Activity activity) {

        mActivity = activity;

        // Initialize media player
        mMediaPlayer = new MediaPlayer();
        // Attach on completion listener
        mMediaPlayer.setOnCompletionListener(this);

        // Attach on click listener to play/pause button
        Button btn = (Button) mActivity.findViewById(R.id.btn_play);
        btn.setOnClickListener(this);
    }

    public void attachMediaPlayerSeekBar(SeekBar seekBar, TextView currentTime, TextView totalTime) {

        // Prepare seek bar for the selected song
        mSeekBar = seekBar;
        mSeekBar.setClickable(true);
        mSeekBar.setOnSeekBarChangeListener(this);

        // Init seek bar labels
        mSeekBarCurrentTimeView = currentTime;
        mSeekBarTotalTimeView = totalTime;

        mSeekBarCurrentTimeView.setText(Helper.songTimeFormat(0));
        mSeekBarTotalTimeView.setText(Helper.songTimeFormat(0));
    }

    /**
     *
     * @param musicFile
     */
    public void openMusicFile(DanceBotMusicFile musicFile) {

        // Bind music file as a lot information is needed later
        mMusicFile = musicFile;
        final String songPath = mMusicFile.getSongPath();

        // Retrieve song from song path and resolve to URI
        Uri songUri = Uri.fromFile(new File(songPath));
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(mActivity, songUri);
            mMediaPlayer.prepare();
            mIsReady = true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Store other important music file properties
        mTotalTime = mMusicFile.getDurationInMiliSecs();

        // Update max seekbar
        if (mSeekBar != null) {
            mSeekBar.setMax(mMediaPlayer.getDuration());
        }

        // Update total time view
        if (mSeekBarTotalTimeView != null) {
            mSeekBarTotalTimeView.setText(Helper.songTimeFormat(mTotalTime));
        }
    }

    /************************************
     * ScrollMediaPlayerMethods Interface
     ************************************/

    @Override
    public boolean isPlaying() {
        return mIsPlaying;
    }

    /**
     * Get the current playback position in milliseconds
     *
     * @return position in milliseconds
     */
    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void setSeekBarProgress(int progress) {
        // Update seek bar
        if (mSeekBar != null) {
            mSeekBar.setProgress(progress);
        }

        // Update seek bar text view current time
        if (mSeekBarCurrentTimeView != null) {
            mSeekBarCurrentTimeView.setText(Helper.songTimeFormat(progress));
        }
    }

    @Override
    public int getSeekBarProgress() {
        if (mSeekBar != null) {
            return mSeekBar.getProgress();
        }
        return 0;
    }

    @Override
    public int getTotalTime() {
        return mTotalTime;
    }

    @Override
    public int getSampleRate() {
        return mMusicFile.getSampleRate();
    }

    @Override
    public void onClick(View v) {

        if (mIsReady) {
            mIsPlaying = !mIsPlaying;
            if (mIsPlaying) {

                mMediaPlayer.start();

                // Set seek bar progress to current song position
                int currentTime = mMediaPlayer.getCurrentPosition();
                if (mSeekBar != null) {
                    mSeekBar.setProgress(currentTime);
                }

                // TODO: More elegant solution?
                // Notify automatic scroll listener when media player progressed
                if (DanceBotEditorManager.getInstance().getAutomaticScrollHandler() != null) {
                    DanceBotEditorManager.getInstance().notifyAutomaticScrollHandler();
                }

            } else {

                mMediaPlayer.pause();
            }

            // Get media player play/pause button
            mPlayPauseButton = (Button) v;

            // Update button text value
            if (mIsPlaying) {
                mPlayPauseButton.setText(R.string.txt_pause);
            } else {
                mPlayPauseButton.setText(R.string.txt_play);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        Log.d(LOG_TAG, "seekBar: on progress changed");

        // Notify automatic scroll listener when media player progressed
        if (DanceBotEditorManager.getInstance().getAutomaticScrollHandler() != null) {
            DanceBotEditorManager.getInstance().notifyAutomaticScrollHandler();
        }

        // If user interaction, set media player progress
        if (fromUser) {
            mMediaPlayer.seekTo(progress);
            Log.d(LOG_TAG, "fromUser: on progress changed");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if (mPlayPauseButton != null) {
            // Set playing flag
            mIsPlaying = false;
            mPlayPauseButton.setText(R.string.txt_play);
            // Rewind media player to the start
            mMediaPlayer.seekTo(0);
        }
    }

    /**
     * Stop media player playback and release resource
     */
    public void cleanUp() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
