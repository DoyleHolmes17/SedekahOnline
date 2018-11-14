package example.com.sedekahonline.feature.alquran.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import example.com.sedekahonline.R;

import example.com.sedekahonline.feature.alquran.model.AyahWord;
import example.com.sedekahonline.feature.alquran.model.Word;
import example.com.sedekahonline.feature.alquran.settings.Config;

import java.util.ArrayList;


/**
 * Created by Sadmansamee on 7/19/15.
 */
public class AyahWordAdapter extends RecyclerView.Adapter<AyahWordAdapter.AyahViewHolder> {

    static boolean showTranslation;
    static boolean wordByWord;

    static int fontSizeArabic;
    static int fontSizeTranslation;
    static Typeface corpusTypeface;
    static String[] corpusArabicTypeArray;
    public Context context;
    long surah_id;
    private ArrayList<AyahWord> ayahWordArrayList;

    public AyahWordAdapter(ArrayList<AyahWord> ayahWordArrayList, Context context, long surah_id) {


        this.ayahWordArrayList = ayahWordArrayList;
        this.context = context;
        this.surah_id = surah_id;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        showTranslation = sharedPreferences.getBoolean(Config.SHOW_TRANSLATION, Config.defaultShowTranslation);
        wordByWord = sharedPreferences.getBoolean(Config.WORD_BY_WORD, Config.defaultWordByWord);
        fontSizeArabic = Integer.parseInt(sharedPreferences.getString(Config.FONT_SIZE_ARABIC, Config.defaultFontSizeArabic));
        fontSizeTranslation = Integer.parseInt(sharedPreferences.getString(Config.FONT_SIZE_TRANSLATION, Config.defaultFontSizeTranslation));
        corpusTypeface = Typeface.createFromAsset(context.getResources().getAssets(), "amiri.ttf");
    }


    @Override
    public int getItemCount() {
        return ayahWordArrayList.size();
    }

    @Override
    public long getItemId(int position) {

        AyahWord ayahWord = ayahWordArrayList.get(position);
        long itemId = 1;

        for (Word word : ayahWord.getWord()) {
            itemId = word.getVerseId();
        }
        return itemId;
    }

    @Override
    public AyahWordAdapter.AyahViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ayah_word, parent, false);
        AyahWordAdapter.AyahViewHolder viewHolder = new AyahWordAdapter.AyahViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AyahWordAdapter.AyahViewHolder holder, int position) {

        final AyahWord ayahWord = ayahWordArrayList.get(position);

        // holder.verse_idTextView.setText("\uFD3F" + intToArabic(ayahWord.getQuranVerseId()) + "\uFD3E");
        holder.verse_idTextView.setText("(" + Long.toString(ayahWord.getQuranVerseId()) + ")");


            holder.arabic_textView.setText(ayahWord.getQuranArabic());
            // holder.arabic_textView.setTypeface(typeface);
            holder.arabic_textView.setTextSize(fontSizeArabic);
            holder.arabic_textView.setVisibility(View.VISIBLE);
//        }

        if (showTranslation) {
            holder.translate_textView.setText(ayahWord.getQuranTranslate());
            holder.translate_textView.setTextSize(fontSizeTranslation);
            holder.translate_textView.setVisibility(View.VISIBLE);

        }


    }
    
    public static class AyahViewHolder extends RecyclerView.ViewHolder {

        public TextView verse_idTextView;
        public TextView translate_textView;
        public TextView arabic_textView;

        public AyahViewHolder(View view) {
            super(view);
            verse_idTextView = (TextView) view.findViewById(R.id.verse_id_textView);
            translate_textView = (TextView) view.findViewById(R.id.translate_textView);
            arabic_textView = (TextView) view.findViewById(R.id.arabic_textView);
        }

    }


}