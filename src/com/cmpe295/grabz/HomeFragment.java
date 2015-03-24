package com.cmpe295.grabz;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment {

	public static final String HomeTabTitle = "Grabz";
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String LOG_PREFIX = "HomeFragment";
	public static final String TAG_ID = "TAG_ID";

	private TextView mTextView;
	private NfcAdapter mNfcAdapter;

	private static final String ARG_SECTION_NUMBER = "section_number";

	public HomeFragment() {
	}

	public static HomeFragment newInstance(int sectionNumber) {
		HomeFragment fragment = new HomeFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		getActivity().setTitle(HomeTabTitle);
		mTextView = (TextView) rootView.findViewById(R.id.textView_message);

		Context parentCtx = getActivity().getApplicationContext();
		mNfcAdapter = NfcAdapter.getDefaultAdapter(parentCtx);

		if (mNfcAdapter == null) {

			Toast.makeText(parentCtx, "Device doesn't support NFC.",
					Toast.LENGTH_LONG).show();
			getActivity().finish();
			return rootView;
		}

		if (!mNfcAdapter.isEnabled()) {
			mTextView.setText("NFC is disabled.");
		} else {
			mTextView.setText(R.string.message);
		}

		handleIntent(getActivity().getIntent(), parentCtx);

		return rootView;

	}

	private void handleIntent(Intent intent, Context context) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {

				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				new NdefReaderTask(context).execute(tag);

			} else {
				Log.d(LOG_PREFIX, "Wrong mime type: " + type);
			}
		}
	}

	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

		private Context parentCtx;

		public NdefReaderTask(Context context) {
			parentCtx = context;
		}

		@Override
		protected String doInBackground(Tag... params) {
			Tag tag = params[0];

			Ndef ndef = Ndef.get(tag);
			if (ndef == null) {
				return null;
			}

			NdefMessage ndefMessage = ndef.getCachedNdefMessage();

			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) {
				if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN
						&& Arrays.equals(ndefRecord.getType(),
								NdefRecord.RTD_TEXT)) {
					try {
						return readText(ndefRecord);
					} catch (UnsupportedEncodingException e) {
						Log.e(LOG_PREFIX, "Unsupported Encoding", e);
					}
				}
			}

			return null;
		}

		private String readText(NdefRecord record)
				throws UnsupportedEncodingException {
			/*
			 * See NFC forum specification for "Text Record Type Definition" at
			 * 3.2.1
			 * 
			 * http://www.nfc-forum.org/specs/
			 * 
			 * bit_7 defines encoding bit_6 reserved for future use, must be 0
			 * bit_5..0 length of IANA language code
			 */

			byte[] payload = record.getPayload();

			// Get the Text Encoding
			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8"
					: "UTF-16";

			// Get the Language Code
			int languageCodeLength = payload[0] & 0063;

			// String languageCode = new String(payload, 1, languageCodeLength,
			// "US-ASCII");
			// e.g. "en"

			// Get the Text
			return new String(payload, languageCodeLength + 1, payload.length
					- languageCodeLength - 1, textEncoding);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Log.d(LOG_PREFIX, "Tag content is: " + result);
				Intent intent = new Intent(parentCtx,
						ItemsInAisleActivity.class);
				intent.putExtra(TAG_ID, result);
				startActivity(intent);
			} else {
				Log.e(LOG_PREFIX, "Tag reading failed");
				mTextView.setText("Could not read the tag.");

			}
		}
	}

}
