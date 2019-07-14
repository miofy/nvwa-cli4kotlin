package com.github.nvwa.cli4kotlin

import com.github.nvwa.cli4kotlin.exception.*
import com.github.nvwa.cli4kotlin.option.*
import com.sun.xml.internal.fastinfoset.util.StringArray
import java.util.*

/**
 * 命令
 *
 * @param name 命令名称
 * @param desc 命令描述信息
 */
open class Command(private val name: String, private val desc: String) {

  // 选项
  private val options = HashMap<String, Option<*>>(10)
  // 必须选项
  private val required = HashMap<String, Option<*>>(10)
  // 选项值
  private var values = HashMap<String, Option<*>>(10)
  // 选项帮助信息
  private var help = StringArray(1, 10, true)

  /**
   * 添加命令选项。添加选项格式、帮助信息以及必须项。
   *
   * @param option 选项
   * @return 当前命名
   */
  fun <T> addOption(option: Option<T>): Command {

    // 缓存短格式选项
    if (option.shortForm != null)
      options["-" + option.shortForm] = option

    // 缓存选项帮助信息
    if (!options.containsKey(option.longForm)) {
      val helpDesc = option.getHelp()
      if (helpDesc != null) help.add(helpDesc)
    }

    // 缓存必须选项
    if (option.isRequired && !required.containsKey(option.longForm))
      required[option.longForm] = option

    // 缓存长格式选项（必须存在）
    options["--" + option.longForm] = option

    return this
  }

  /**
   * 添加参数值
   *
   * @param option 选项
   * @param valueArg 值参数
   */
  @Throws(IllegalOptionValueException::class)
  private fun <T> addValue(option: Option<T>, valueArg: String) {
    option.setValue(valueArg)
    // if (values.containsKey(option.longForm)) values.remove(option.longForm)
    values[option.longForm] = option
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T> getValue(option: Option<T>, default: T? = null): T? {
    val found = values[option.longForm]
    return if (found == null) default else (found.getValue() as T) ?: default
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T> getValue(shortName: Char, default: T? = null): T? {
    val option = options["-" + shortName.toString()] as Option<T>?
    return if (option == null) null else getValue(option, default)
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T> getValue(longName: String, default: T? = null): T? {
    val option = options["--$longName"] as Option<T>?
    return if (option == null) null else getValue(option, default)
  }

  // 添加不同类型的选项

  fun addStringOption(
          longForm: String,
          isRequired: Boolean,
          shortForm: Char? = null,
          help: String? = null
  ): Command = addOption(StringOption(longForm, isRequired, shortForm, help))

  fun addIntegerOption(
          longForm: String,
          isRequired: Boolean,
          shortForm: Char? = null,
          help: String? = null
  ): Command = addOption(IntegerOption(longForm, isRequired, shortForm, help))

  fun addLongOption(
          longForm: String,
          isRequired: Boolean,
          shortForm: Char? = null,
          help: String? = null
  ): Command = addOption(LongOption(longForm, isRequired, shortForm, help))

  fun addDoubleOption(
          longForm: String,
          isRequired: Boolean,
          shortForm: Char? = null,
          help: String? = null
  ): Command = addOption(DoubleOption(longForm, isRequired, shortForm, help))

  fun addBooleanOption(
          longForm: String,
          isRequired: Boolean,
          shortForm: Char? = null,
          help: String? = null
  ): Command = addOption(BooleanOption(longForm, isRequired, shortForm, help))

  // 获取不同类型选项值

  fun getStringValue(
          longForm: String,
          default: String? = null
  ): String? = getValue(longForm, default)

  fun getStringValue(
          shortForm: Char,
          default: String? = null
  ): String? = getValue(shortForm, default)

  fun getIntegerValue(
          longForm: String,
          default: Int? = null
  ): Int? = getValue(longForm, default)

  fun getIntegerValue(
          shortForm: Char,
          default: Int? = null
  ): Int? = getValue(shortForm, default)

  fun getLongValue(
          longForm: String,
          default: Long? = null
  ): Long? = getValue(longForm, default)

  fun getLongValue(
          shortForm: Char,
          default: Long? = null
  ): Long? = getValue(shortForm, default)

  fun getDoubleValue(
          longForm: String,
          default: Double? = null
  ): Double? = getValue(longForm, default)

  fun getDoubleValue(
          shortForm: Char,
          default: Double? = null
  ): Double? = getValue(shortForm, default)

  fun getBooleanValue(
          longForm: String,
          default: Boolean? = null
  ): Boolean? = getValue(longForm, default)

  fun getBooleanValue(
          shortForm: Char,
          default: Boolean? = null
  ): Boolean? = getValue(shortForm, default)

  /**
   * 解析命令行参数
   *
   * @param args 待解析参数表
   */
  @Throws(UnknownOptionException::class,
          IllegalOptionValueException::class,
          UnknownSubOptionException::class,
          NotFlagException::class,
          RequiredOptionException::class)
  fun parse(args: Array<String>) {
    var position = 0

    while (position < args.size) {
      // 传入选项名
      var arg = args[position]

      if (arg.startsWith("-")) {
        if (arg == "--") {
          throw IllegalOptionFormatException(arg)
        } else if (arg.startsWith("--")) {
          // 处理长格式选项
          handleLongOption(arg)
        } else if (arg.startsWith("-")) {
          // 传入选项值
          val next = if (position < args.size - 1) args[++position] else null
          // arg.length保证是短格式选项，next保证选项值存在且合法
          if (arg.length == 2 && next != null && !next.startsWith("-")) {
            // 处理短格式参数
            handleShortOption(arg, next)
          } else {
            // 处理无选项值
            handleFlags(arg)
          }
        }
      }
      position++
    }

    for ((_: String, option: Option<*>) in required) {
      if (!values.containsKey(option.longForm)) {
        throw RequiredOptionException(option)
      }
    }
  }

  /**
   * 处理长格式参数值
   * <p>
   * 例如：<code>--size=10</code>
   * </p>
   */
  private fun handleLongOption(line: String) {
    val index = line.indexOf("=")
    var key: String?
    var value: String?
    if (index != -1) {
      // "--"与"="之间的是选项名
      key = line.substring(2, index)
      // "="后的是选项值
      value = line.substring(index + 1)
    } else {
      key = line.substring(2)
      value = null
    }

    val option = options["--$key"]
    if (option == null) {
      throw UnknownOptionException(key)
    } else if (option.withValue && value != null) {
      addValue(option, value)
    } else {
      // 选项没有withValue需要，即使传入值也不使用
      addValue(option, "")
    }
  }

  /**
   * 处理短格式参数值
   */
  private fun handleShortOption(key: String, value: String) {
    val option = options[key]
    if (option == null) throw UnknownOptionException(key.substring(1))
    else this.addValue(option, value)
  }

  /**
   * 处理短格式参数
   */
  private fun handleFlags(line: String) {
    for (i in 1 until line.length) {
      val option = options["-" + line[i].toString()]
      if (option == null) {
        throw UnknownSubOptionException(line, line[i])
      } else if (option.withValue) {
        throw NotFlagException(line, option)
      }
      addValue(option, "")
    }
  }

  /**
   * 返回习惯用法
   *
   * @return 惯用法
   */
  private fun getUsage(): String {
    val formats = StringArray(1, 10, true)
    var counter = 1
    for ((key: String, option: Option<*>) in options)
      if (key.startsWith("--")) {
        var format: String =
                if (option.shortForm == null)
                  if (option.withValue)
                    "{--" + option.longForm + "=<value${counter++}>}"
                  else "{--" + option.longForm + "}"
                else
                  if (option.withValue)
                    "{--" + option.longForm + "=<value$counter>, -" +
                            option.shortForm + " value${counter++}}"
                  else "{--" + option.longForm + ", -" + option.shortForm + "}"

        // "[]"表示可选参数
        if (!option.isRequired) {
          format = "[$format]"
        }
        formats.add(format)
      }

    return "Usage: ${this.name} " +
            if (formats.size > 0) formats.join(" ") else ""
  }

  private fun getDescription(): String {
    return "Description: ${this.desc}"
  }

  private fun getOptionsDescription(): String =
          if (help.size > 0) "Options:" + "\n\t" +
                  help.join("\n\t") else ""

  open fun getHelp(): String = getUsage() + "\n" +
          getDescription() + "\n" + getOptionsDescription()

  private fun StringArray.join(separator: String): String {
    var result = ""
    val size = this.size
    if (size > 0) {
      result += this.get(0)
      if (size > 1)
        for (index in 1..(size - 1))
          result += separator + this.get(index)
    }
    return result
  }
}