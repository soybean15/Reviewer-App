package com.devour.reviewerapp.activities.components

import android.graphics.Color
import com.neo.highlight.core.Highlight
import com.neo.highlight.util.scheme.ColorScheme
import java.util.regex.Pattern

class SyntaxHighlighter {


    companion object{

        fun highlight():Highlight{
             val highlight =  Highlight()
            highlight.addScheme(
                ColorScheme(
                    Pattern.compile("\\b([Jj])ava\\b"),
                    Color.parseColor("#FC0400")
                ),
                ColorScheme(
                    Pattern.compile("\\b([Kk])otlin\\b"),
                    Color.parseColor("#FC8500")
                ),
                ColorScheme(
                    Pattern.compile("\\bclass\\b"),
                    Color.parseColor("#ad692d")
                ),
                ColorScheme(
                    //
                    Pattern.compile("(\\blong\\b)|(\\breturn\\b)|(\\bvoid\\b)|(\\bpublic\\b)|(\\bstatic\\b)|(\\bfloat\\b)|(\\bint\\b)|(\\bboolean\\b)|(\\bnull\\b)|(\\bvar\\b)|(\\bchar\\b)|(\\bdouble\\b)|(\\bif\\b)|(\\boverride\\b)|(\\bfun\\b)|(\\bval\\b)|(\\belse\\b)|(\\belif\\b)|(\\bdo\\b)|(\\bwhile\\b)|(\\bfor\\b)"),
                    Color.parseColor("#ad692d")
                ),
                ColorScheme(
                    Pattern.compile("(\\bnew\\b)"),
                    Color.parseColor("#fc2003")
                ),
                ColorScheme(
                    Pattern.compile("\"(.*?)\""),
                    Color.parseColor("#fca903")
                ),ColorScheme(
                    Pattern.compile("(//.+)"),
                    Color.parseColor("#5c5756")
                ),
                ColorScheme(
                    Pattern.compile("(#.+)"),
                    Color.parseColor("#0b7a34")
                ),


            //#5c5756


            )



            return highlight
        }
    }

}