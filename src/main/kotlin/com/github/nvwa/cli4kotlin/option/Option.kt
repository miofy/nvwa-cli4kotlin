package com.github.nvwa.cli4kotlin.option

import com.github.nvwa.cli4kotlin.exception.IllegalOptionNameException
import com.github.nvwa.cli4kotlin.exception.IllegalOptionValueException
import java.util.regex.Pattern

/**
 * 命令选项。
 * <p>
 * 必须保证匹配长格式参数，若长整数匹配不上，直接抛出非法选项名异常。
 * 在长格式参数匹配上后，再看是否有端格式参数，如果存在则进行匹配，判断规则同长格式。
 * </p>
 *
 * @param longForm 长格式
 * @param withValue 选项是否必须携带值
 * @param isRequired 是否为必须选项，默认不需要
 * @param shortForm 短格式，默认null
 * @param helpDesc 帮助信息，默认null
 */
@Suppress("LeakingThis")
abstract class Option<T> private constructor(
    val longForm: String, val withValue: Boolean,
    val isRequired: Boolean = false, val shortForm: String? = null,
    private val helpDesc: String? = null) {

  // 解析字符串参数所得的具体类型值
  private var value: T? = null

  // "--"型，长格式
  private val longFormPattern = Pattern.compile(
      "^([a-z](?:[a-z0-9_\\-]*[a-z0-9])?)$", Pattern.CASE_INSENSITIVE)
  // "-"型，短格式
  private val shortFormPattern = Pattern.compile(
      "^[a-z]$", Pattern.CASE_INSENSITIVE)

  init {
    val longFormMatcher = longFormPattern.matcher(longForm)
    if (!longFormMatcher.matches()) {
      // 构造函数可能未初始化完毕，调用其它方法并传递this存在一定的危险性。但是此处可以使用。
      throw IllegalOptionNameException(this)
    }

    if (shortForm != null) {
      val shortFormMatcher = shortFormPattern.matcher(shortForm)
      if (!shortFormMatcher.matches()) {
        // 同上
        throw IllegalOptionNameException(this)
      }
    }
  }

  protected constructor(
      longForm: String,
      withValue: Boolean,
      isRequired: Boolean,
      shortForm: Char? = null,
      helpDesc: String? = null) :
      this(longForm, withValue, isRequired, shortForm?.toString(), helpDesc)

  /**
   * 返回选项帮助信息
   *
   * @return 选项帮助信息
   */
  fun getHelp(): String? {
    return if (helpDesc == null) null else {
      val options = (if (shortForm != null) "-$shortForm, " else "") + "--" + longForm + ":"
      val tabs = 4 - (options.length / 4)
      options + "\t".repeat(tabs) + helpDesc + (if (isRequired) " (is required)" else "")
    }
  }

  /**
   * 设置选项值，并解析选项值
   *
   * @param arg 选项参数
   * @throws IllegalOptionValueException
   * 如果选项需要携带值，但是传入参数为空，则抛出异常。
   */
  @Throws(IllegalOptionValueException::class)
  fun setValue(arg: String) {
    if (this.withValue && arg == "") throw IllegalOptionValueException(this, "")
    value = parse(arg)
  }

  /**
   * 返回解析完后的选项值
   *
   * @return T 解析后的选项值
   */
  fun getValue(): T {
    return value!!
  }

  /**
   * 解析参数。选项派生类具体实现不同类型参数的解析任务
   */
  @Throws(IllegalOptionValueException::class)
  protected abstract fun parse(arg: String): T


  override fun toString(): String {
    return (if (shortForm != null) "-$shortForm, " else "") + "--" + longForm
  }
}