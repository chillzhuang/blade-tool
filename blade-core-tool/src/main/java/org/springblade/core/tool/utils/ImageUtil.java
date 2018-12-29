/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springblade.core.tool.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.tool.support.IMultiOutputStream;
import org.springblade.core.tool.support.ImagePosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.net.URL;

/**
 * 图片工具类
 *
 * @author Chill
 */
public final class ImageUtil {

	/**
	 * Logger for this class
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);

	/**
	 * 默认输出图片类型
	 */
	public static final String DEFAULT_IMG_TYPE = "JPEG";

	private ImageUtil() {

	}

	/**
	 * 转换输入流到byte
	 *
	 * @param src  源
	 * @param type 类型
	 * @return byte[]
	 * @throws IOException 异常
	 */
	public static byte[] toByteArray(BufferedImage src, String type) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(src, defaultString(type, DEFAULT_IMG_TYPE), os);
		return os.toByteArray();
	}

	/**
	 * 获取图像内容
	 *
	 * @param srcImageFile 文件路径
	 * @return BufferedImage
	 */
	public static BufferedImage readImage(String srcImageFile) {
		try {
			return ImageIO.read(new File(srcImageFile));
		} catch (IOException e) {
			LOGGER.error("Error readImage", e);
		}
		return null;
	}

	/**
	 * 获取图像内容
	 *
	 * @param srcImageFile 文件
	 * @return BufferedImage
	 */
	public static BufferedImage readImage(File srcImageFile) {
		try {
			return ImageIO.read(srcImageFile);
		} catch (IOException e) {
			LOGGER.error("Error readImage", e);
		}
		return null;
	}

	/**
	 * 获取图像内容
	 *
	 * @param srcInputStream 输入流
	 * @return BufferedImage
	 */
	public static BufferedImage readImage(InputStream srcInputStream) {
		try {
			return ImageIO.read(srcInputStream);
		} catch (IOException e) {
			LOGGER.error("Error readImage", e);
		}
		return null;
	}

	/**
	 * 获取图像内容
	 *
	 * @param url URL地址
	 * @return BufferedImage
	 */
	public static BufferedImage readImage(URL url) {
		try {
			return ImageIO.read(url);
		} catch (IOException e) {
			LOGGER.error("Error readImage", e);
		}
		return null;
	}


	/**
	 * 缩放图像（按比例缩放）
	 *
	 * @param src    源图像
	 * @param output 输出流
	 * @param type   类型
	 * @param scale  缩放比例
	 * @param flag   缩放选择:true 放大; false 缩小;
	 */
	public final static void zoomScale(BufferedImage src, OutputStream output, String type, double scale, boolean flag) {
		try {
			// 得到源图宽
			int width = src.getWidth();
			// 得到源图长
			int height = src.getHeight();
			if (flag) {
				// 放大
				width = Long.valueOf(Math.round(width * scale)).intValue();
				height = Long.valueOf(Math.round(height * scale)).intValue();
			} else {
				// 缩小
				width = Long.valueOf(Math.round(width / scale)).intValue();
				height = Long.valueOf(Math.round(height / scale)).intValue();
			}
			Image image = src.getScaledInstance(width, height, Image.SCALE_DEFAULT);
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = tag.getGraphics();

			g.drawImage(image, 0, 0, null);
			g.dispose();

			ImageIO.write(tag, defaultString(type, DEFAULT_IMG_TYPE), output);

			output.close();
		} catch (IOException e) {
			LOGGER.error("Error in zoom image", e);
		}
	}

	/**
	 * 缩放图像（按高度和宽度缩放）
	 *
	 * @param src       源图像
	 * @param output    输出流
	 * @param type      类型
	 * @param height    缩放后的高度
	 * @param width     缩放后的宽度
	 * @param bb        比例不对时是否需要补白：true为补白; false为不补白;
	 * @param fillColor 填充色，null时为Color.WHITE
	 */
	public final static void zoomFixed(BufferedImage src, OutputStream output, String type, int height, int width, boolean bb, Color fillColor) {
		try {
			double ratio = 0.0;
			Image itemp = src.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
			// 计算比例
			if (src.getHeight() > src.getWidth()) {
				ratio = Integer.valueOf(height).doubleValue() / src.getHeight();
			} else {
				ratio = Integer.valueOf(width).doubleValue() / src.getWidth();
			}
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
			itemp = op.filter(src, null);

			if (bb) {
				//补白
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				Color fill = fillColor == null ? Color.white : fillColor;
				g.setColor(fill);
				g.fillRect(0, 0, width, height);
				if (width == itemp.getWidth(null)) {
					g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null), itemp.getHeight(null), fill, null);
				} else {
					g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null), fill, null);
				}
				g.dispose();
				itemp = image;
			}
			// 输出为文件
			ImageIO.write((BufferedImage) itemp, defaultString(type, DEFAULT_IMG_TYPE), output);
			// 关闭流
			output.close();
		} catch (IOException e) {
			LOGGER.error("Error in zoom image", e);
		}
	}

	/**
	 * 图像裁剪(按指定起点坐标和宽高切割)
	 *
	 * @param src    源图像
	 * @param output 切片后的图像地址
	 * @param type   类型
	 * @param x      目标切片起点坐标X
	 * @param y      目标切片起点坐标Y
	 * @param width  目标切片宽度
	 * @param height 目标切片高度
	 */
	public final static void crop(BufferedImage src, OutputStream output, String type, int x, int y, int width, int height) {
		try {
			// 源图宽度
			int srcWidth = src.getHeight();
			// 源图高度
			int srcHeight = src.getWidth();
			if (srcWidth > 0 && srcHeight > 0) {
				Image image = src.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
				// 四个参数分别为图像起点坐标和宽高
				ImageFilter cropFilter = new CropImageFilter(x, y, width, height);
				Image img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
				BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = tag.getGraphics();
				g.drawImage(img, 0, 0, width, height, null);
				g.dispose();
				// 输出为文件
				ImageIO.write(tag, defaultString(type, DEFAULT_IMG_TYPE), output);
				// 关闭流
				output.close();
			}
		} catch (Exception e) {
			LOGGER.error("Error in cut image", e);
		}
	}

	/**
	 * 图像切割（指定切片的行数和列数）
	 *
	 * @param src   源图像地址
	 * @param mos   切片目标文件夹
	 * @param type  类型
	 * @param prows 目标切片行数。默认2，必须是范围 [1, 20] 之内
	 * @param pcols 目标切片列数。默认2，必须是范围 [1, 20] 之内
	 */
	public final static void sliceWithNumber(BufferedImage src, IMultiOutputStream mos, String type, int prows, int pcols) {
		try {
			int rows = prows <= 0 || prows > 20 ? 2 : prows;
			int cols = pcols <= 0 || pcols > 20 ? 2 : pcols;
			// 源图宽度
			int srcWidth = src.getHeight();
			// 源图高度
			int srcHeight = src.getWidth();
			if (srcWidth > 0 && srcHeight > 0) {
				Image img;
				ImageFilter cropFilter;
				Image image = src.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
				// 每张切片的宽度
				int destWidth = (srcWidth % cols == 0) ? (srcWidth / cols) : (srcWidth / cols + 1);
				// 每张切片的高度
				int destHeight = (srcHeight % rows == 0) ? (srcHeight / rows) : (srcHeight / rows + 1);
				// 循环建立切片
				// 改进的想法:是否可用多线程加快切割速度
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						// 四个参数分别为图像起点坐标和宽高
						cropFilter = new CropImageFilter(j * destWidth, i * destHeight, destWidth, destHeight);
						img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
						BufferedImage tag = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
						Graphics g = tag.getGraphics();
						// 绘制缩小后的图
						g.drawImage(img, 0, 0, null);
						g.dispose();
						// 输出为文件
						ImageIO.write(tag, defaultString(type, DEFAULT_IMG_TYPE), mos.buildOutputStream(i, j));
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error in slice image", e);
		}
	}

	/**
	 * 图像切割（指定切片的宽度和高度）
	 *
	 * @param src         源图像地址
	 * @param mos         切片目标文件夹
	 * @param type        类型
	 * @param pdestWidth  目标切片宽度。默认200
	 * @param pdestHeight 目标切片高度。默认150
	 */
	public final static void sliceWithSize(BufferedImage src, IMultiOutputStream mos, String type, int pdestWidth, int pdestHeight) {
		try {
			int destWidth = pdestWidth <= 0 ? 200 : pdestWidth;
			int destHeight = pdestHeight <= 0 ? 150 : pdestHeight;
			// 源图宽度
			int srcWidth = src.getHeight();
			// 源图高度
			int srcHeight = src.getWidth();
			if (srcWidth > destWidth && srcHeight > destHeight) {
				Image img;
				ImageFilter cropFilter;
				Image image = src.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
				// 切片横向数量
				int cols = (srcWidth % destWidth == 0) ? (srcWidth / destWidth) : (srcWidth / destWidth + 1);
				// 切片纵向数量
				int rows = (srcHeight % destHeight == 0) ? (srcHeight / destHeight) : (srcHeight / destHeight + 1);
				// 循环建立切片
				// 改进的想法:是否可用多线程加快切割速度
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						// 四个参数分别为图像起点坐标和宽高
						cropFilter = new CropImageFilter(j * destWidth, i * destHeight, destWidth, destHeight);
						img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
						BufferedImage tag = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
						Graphics g = tag.getGraphics();
						// 绘制缩小后的图
						g.drawImage(img, 0, 0, null);
						g.dispose();
						// 输出为文件
						ImageIO.write(tag, defaultString(type, DEFAULT_IMG_TYPE), mos.buildOutputStream(i, j));
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error in slice image", e);
		}
	}

	/**
	 * 图像类型转换：GIF-JPG、GIF-PNG、PNG-JPG、PNG-GIF(X)、BMP-PNG
	 *
	 * @param src        源图像地址
	 * @param formatName 包含格式非正式名称的 String：如JPG、JPEG、GIF等
	 * @param output     目标图像地址
	 */
	public final static void convert(BufferedImage src, OutputStream output, String formatName) {
		try {
			// 输出为文件
			ImageIO.write(src, formatName, output);
			// 关闭流
			output.close();
		} catch (Exception e) {
			LOGGER.error("Error in convert image", e);
		}
	}

	/**
	 * 彩色转为黑白
	 *
	 * @param src    源图像地址
	 * @param output 目标图像地址
	 * @param type      类型
	 */
	public final static void gray(BufferedImage src, OutputStream output, String type) {
		try {
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			ColorConvertOp op = new ColorConvertOp(cs, null);
			src = op.filter(src, null);
			// 输出为文件
			ImageIO.write(src, defaultString(type, DEFAULT_IMG_TYPE), output);
			// 关闭流
			output.close();
		} catch (IOException e) {
			LOGGER.error("Error in gray image", e);
		}
	}

	/**
	 * 给图片添加文字水印
	 *
	 * @param src      源图像
	 * @param output   输出流
	 * @param type      类型
	 * @param text     水印文字
	 * @param font     水印的字体
	 * @param color    水印的字体颜色
	 * @param position 水印位置 {@link ImagePosition}
	 * @param x        修正值
	 * @param y        修正值
	 * @param alpha    透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 */
	public final static void textStamp(BufferedImage src, OutputStream output, String type, String text, Font font, Color color
		, int position, int x, int y, float alpha) {
		try {
			int width = src.getWidth(null);
			int height = src.getHeight(null);
			BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(src, 0, 0, width, height, null);
			g.setColor(color);
			g.setFont(font);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			// 在指定坐标绘制水印文字
			ImagePosition boxPos = new ImagePosition(width, height, calcTextWidth(text) * font.getSize(), font.getSize(), position);
			g.drawString(text, boxPos.getX(x), boxPos.getY(y));
			g.dispose();
			// 输出为文件
			ImageIO.write((BufferedImage) image, defaultString(type, DEFAULT_IMG_TYPE), output);
			// 关闭流
			output.close();
		} catch (Exception e) {
			LOGGER.error("Error in textStamp image", e);
		}
	}

	/**
	 * 给图片添加图片水印
	 *
	 * @param src      源图像
	 * @param output   输出流
	 * @param type      类型
	 * @param stamp    水印图片
	 * @param position 水印位置 {@link ImagePosition}
	 * @param x        修正值
	 * @param y        修正值
	 * @param alpha    透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 */
	public final static void imageStamp(BufferedImage src, OutputStream output, String type, BufferedImage stamp
		, int position, int x, int y, float alpha) {
		try {
			int width = src.getWidth();
			int height = src.getHeight();
			BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(src, 0, 0, width, height, null);
			// 水印文件
			int stampWidth = stamp.getWidth();
			int stampHeight = stamp.getHeight();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			ImagePosition boxPos = new ImagePosition(width, height, stampWidth, stampHeight, position);
			g.drawImage(stamp, boxPos.getX(x), boxPos.getY(y), stampWidth, stampHeight, null);
			// 水印文件结束
			g.dispose();
			// 输出为文件
			ImageIO.write((BufferedImage) image, defaultString(type, DEFAULT_IMG_TYPE), output);
			// 关闭流
			output.close();
		} catch (Exception e) {
			LOGGER.error("Error imageStamp", e);
		}
	}

	/**
	 * 计算text的长度（一个中文算两个字符）
	 *
	 * @param text text
	 * @return int
	 */
	public final static int calcTextWidth(String text) {
		int length = 0;
		for (int i = 0; i < text.length(); i++) {
			if (new String(text.charAt(i) + "").getBytes().length > 1) {
				length += 2;
			} else {
				length += 1;
			}
		}
		return length / 2;
	}

	/**
	 * 默认字符串
	 * @param str 字符串
	 * @param defaultStr 默认值
	 * @return
	 */
	public static String defaultString(String str, String defaultStr) {
		return ((str == null) ? defaultStr : str);
	}

}
