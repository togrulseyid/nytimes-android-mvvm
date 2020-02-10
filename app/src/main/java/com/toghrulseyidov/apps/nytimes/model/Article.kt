package com.toghrulseyidov.apps.nytimes.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//https://developer.nytimes.com/docs/articlesearch-product/1/types/Article
@Entity(tableName = "articles")
data class Article(
    @field:PrimaryKey
    @SerializedName("_id")
    val _id: String,
//    @Embedded
//    val byline: Byline?,
    @SerializedName("document_type")
    val documentType: String?,
    @Embedded
    val headline: Headline?,
//    @Embedded
//    val keywords: List<Keyword>?,
//    @Embedded
    val multimedia: List<Multimedia>?,
    @SerializedName("news_desk")
    val newsDesk: String?,
    @SerializedName("print_page")
    val printPage: Int?,
    @SerializedName("pub_date")
    val pubDate: String,
    val score: Int?,
    val snippet: String?,
    @SerializedName("lead_paragraph")
    val leadParagraph: String?,
    val source: String?,
    @SerializedName("type_of_material")
    val typeOfMaterial: String?,
    val uri: String?,
    val web_url: String?,
    @SerializedName("word_count")
    val word_count: Int?
)

//https://developer.nytimes.com/docs/articlesearch-product/1/types/Multimedia
data class Multimedia(
    val caption: String?,
    val credit: String?,
    @SerializedName("crop_name")
    val cropName: String,
    val height: Int?,
    @Embedded
    val legacy: Legacy?,
    val rank: Int?,
    @SerializedName("subtype")
    val subType: String?,
    val type: String?,
    val url: String?,
    val width: Int?
)

data class Legacy(
    @SerializedName("xlarge")
    val xLarge: String,
    @SerializedName("xlargewidth")
    val xLargeWidth: Int,
    @SerializedName("xlargeheight")
    val xLargeHeight: Int
)

//https://developer.nytimes.com/docs/articlesearch-product/1/types/Keyword
data class Keyword(
    val major: String,
    val name: String,
    val rank: Int,
    val value: String
)

//https://developer.nytimes.com/docs/articlesearch-product/1/types/Byline
data class Byline(
    val organization: String?,
    val original: String?,
    @Embedded
    @SerializedName("person")
    val person: List<Person>?
)

//https://developer.nytimes.com/docs/articlesearch-product/1/types/Person
data class Person(
    val firstname: String,
    val lastname: String,
    val middlename: String,
    val organization: String,
    val qualifier: String,
    val rank: Int,
    val role: String,
    val title: String
)

//https://developer.nytimes.com/docs/articlesearch-product/1/types/Headline
data class Headline(
    val content_kicker: String?,
    val kicker: String?,
    val main: String?,
    val name: String?,
    val print_headline: String?,
    val seo: String?,
    val sub: String?
)