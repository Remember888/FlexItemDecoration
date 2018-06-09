package com.ckr.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import static com.ckr.decoration.DecorationLog.Logd;
import static com.ckr.decoration.DecorationLog.Loge;


/**
 * Created by PC大佬 on 2018/1/6.
 */

public class DividerLinearItemDecoration extends BaseItemDecoration {
	private static final String TAG = "LinearItemDecoration";
	private int mDividerPaddingLeft;
	private int mDividerPaddingTop;
	private int mDividerPaddingRight;
	private int mDividerPaddingBottom;
	private boolean isStickyHeader;
	private int mStickyHeaderHeight;
	private Drawable mStickyHeaderDrawable;
	private int textPaddingLeft = 48;//头部文本左边距
	private Paint mHeaderTextPaint;//头部文本画笔
	private float axis;

	//	private Drawable drawable;
//	private int bottom;

	public DividerLinearItemDecoration(Context context) {
		super(context, LINEAR, VERTICAL);
	}

	public DividerLinearItemDecoration(Context context, int orientation) {
		super(context, LINEAR, orientation);
	}

	public DividerLinearItemDecoration(Context context, int orientation, @DrawableRes int drawableId) {
		super(context, LINEAR, orientation, drawableId);
	}

	private DividerLinearItemDecoration(Builder builder) {
		super(builder);
		this.mDividerPaddingLeft = builder.mDividerPaddingLeft;
		this.mDividerPaddingTop = builder.mDividerPaddingTop;
		this.mDividerPaddingRight = builder.mDividerPaddingRight;
		this.mDividerPaddingBottom = builder.mDividerPaddingBottom;
		this.isStickyHeader = builder.isStickyHeader;
		this.mStickyHeaderHeight = builder.mStickyHeaderHeight;
		this.mStickyHeaderDrawable = builder.mStickyHeaderDrawable;
//		drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_decoration);
		if (isStickyHeader) {
			mHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mHeaderTextPaint.setColor(Color.WHITE);//注意：颜色为argb，否则，绘制不出
			mHeaderTextPaint.setTextSize(40);
			mHeaderTextPaint.setTextAlign(Paint.Align.LEFT);
			Paint.FontMetrics fontMetrics = mHeaderTextPaint.getFontMetrics();
			float total = -fontMetrics.ascent + fontMetrics.descent;
			axis = total / 2 - fontMetrics.descent;
			mHeaderContentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mHeaderContentPaint.setColor(Color.TRANSPARENT);
		}
	}

	private Paint mHeaderContentPaint;

	public BaseItemDecoration setDividerPaddingLeft(@IntRange(from = 0) int paddingLeft) {
		this.mDividerPaddingLeft = paddingLeft;
		return this;
	}

	public BaseItemDecoration setDividerPaddingRight(@IntRange(from = 0) int paddingRight) {
		this.mDividerPaddingRight = paddingRight;
		return this;
	}

	public BaseItemDecoration setDividerPaddingTop(@IntRange(from = 0) int paddingTop) {
		this.mDividerPaddingTop = paddingTop;
		return this;
	}

	public BaseItemDecoration setDividerPaddingBottom(@IntRange(from = 0) int paddingBottom) {
		this.mDividerPaddingBottom = paddingBottom;
		return this;
	}

	public BaseItemDecoration setDividerPadding(@IntRange(from = 0) int... padding) {
		for (int i = 0; i < padding.length; i++) {
			if (i == 0) {
				this.mDividerPaddingLeft = padding[i];
			} else if (i == 1) {
				this.mDividerPaddingTop = padding[i];
			} else if (i == 2) {
				this.mDividerPaddingRight = padding[i];
			} else if (i == 3) {
				this.mDividerPaddingBottom = padding[i];
				break;
			}
		}
		return this;
	}

	//绘制竖直分割线
	@Override
	public void drawVertical(Canvas c, RecyclerView parent) {
		final int childCount = parent.getChildCount();
		int itemCount = parent.getAdapter().getItemCount();
		int left = 0;
		int top = 0;
		int right = 0;
		int bottom = 0;
		top = parent.getPaddingTop() + mDividerPaddingTop;
		bottom = parent.getHeight() - parent.getPaddingBottom() - mDividerPaddingBottom;
		boolean leftPosHandle = true;
		boolean rightPosHandle = true;
		boolean isSubDividerHandle = true;
		boolean isRedrawDividerHandle = true;
		for (int i = 0; i < childCount; i++) {
			final View child = parent.getChildAt(i);
			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
					.getLayoutParams();
			//<editor-fold desc="最左边分割线绘制与定制">
			if (!noDrawLeftDivider) {//最左边分割线处理
				if (leftPosHandle) {
					int adapterPosition = parent.getChildAdapterPosition(child);
					if (0 == adapterPosition) {
						leftPosHandle = false;
						Logd(TAG, "drawVertical: !noDrawLeftDivider:" + i + ",adapterPosition:" + adapterPosition);
						right = child.getLeft() - params.rightMargin;
						if (isRedrawLeftDivider) {//最左边分割线的定制
							left = right - mLeftDividerWidth;
							if (mLeftDividerDrawable != null) {
								mLeftDividerDrawable.setBounds(left, top, right, bottom);
								mLeftDividerDrawable.draw(c);
							} else {
								mDivider.setBounds(left, top, right, bottom);
								mDivider.draw(c);
							}
						} else {
							left = right - mDividerWidth;
							mDivider.setBounds(left, top, right, bottom);
							mDivider.draw(c);
						}
					} else {
						leftPosHandle = false;
					}
				}
			}
			//</editor-fold>
			int rightDividerWidth = mDividerWidth;
			//<editor-fold desc="最右边分割线绘制与定制">
			if (noDrawRightDivider) {//最右边分割线处理
				if (rightPosHandle) {
					if (childCount - 1 == i) {
						int adapterPosition = parent.getChildAdapterPosition(child);
						if (itemCount - 1 == adapterPosition) {
							Logd(TAG, "drawVertical: noDrawRightDivider:" + i + ",adapterPosition:" + adapterPosition);
							rightDividerWidth = 0;
							rightPosHandle = false;
						} else {
							rightPosHandle = false;
						}
					}
				}
			} else {
				if (isRedrawRightDivider) {//最右边分割线的定制
					if (rightPosHandle) {
						if (childCount - 1 == i) {
							int adapterPosition = parent.getChildAdapterPosition(child);
							if (itemCount - 1 == adapterPosition) {
								Logd(TAG, "drawVertical: noDrawFooterDivider:" + i + ",adapterPosition:" + adapterPosition);
								rightDividerWidth = mRightDividerWidth;
								rightPosHandle = false;
								if (mRightDividerDrawable != null) {
									left = child.getRight() + params.rightMargin;
									right = left + rightDividerWidth;
									mRightDividerDrawable.setBounds(left, top, right, bottom);
									mRightDividerDrawable.draw(c);
									continue;
								}
							} else {
								rightPosHandle = false;
							}
						}
					}
				}
			}
			//</editor-fold>
			//<editor-fold desc="分割线定制">
			if (isRedrawDivider) {//分割线的定制
				if (isRedrawDividerHandle) {
					int adapterPosition = parent.getChildAdapterPosition(child);
					if (Math.min(mDividerIndex, itemCount - 2) == adapterPosition) {
						isRedrawDividerHandle = false;
						left = child.getRight() + params.rightMargin;
						right = left + mRedrawDividerWidth;
						if (mDividerDrawable != null) {
							mDividerDrawable.setBounds(left, top, right, bottom);
							mDividerDrawable.draw(c);
						} else {
							mDivider.setBounds(left, top, right, bottom);
							mDivider.draw(c);
						}
						continue;
					}
				}
			}
			//</editor-fold>
			//<editor-fold desc="分割线批量定制">
			if (isSubDivider) {//分割线的截取
				if (isSubDividerHandle) {
					int adapterPosition = parent.getChildAdapterPosition(child);
					if (mStartIndex >= itemCount - 1) {
						isSubDivider = false;
					} else {
						if (adapterPosition >= mStartIndex) {
							if (adapterPosition < Math.min(mEndIndex, itemCount - 1)) {
								rightDividerWidth = mSubDividerWidth;
								if (mSubDrawable != null) {
									left = child.getRight() + params.rightMargin;
									right = left + rightDividerWidth;
									mSubDrawable.setBounds(left, top, right, bottom);
									mSubDrawable.draw(c);
									continue;
								}
								if (adapterPosition == mEndIndex - 1) {
									isSubDividerHandle = false;
								}
							} else {
								isSubDividerHandle = false;
							}
						} else {
							isSubDividerHandle = false;
						}
					}
				}
			}
			//</editor-fold>
			left = child.getRight() + params.rightMargin;
			right = left + rightDividerWidth;
			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}

	//绘制水平分割线
	@Override
	public void drawHorizontal(Canvas c, RecyclerView parent) {
		int childCount = parent.getChildCount();//可视item的个数
		int itemCount = parent.getAdapter().getItemCount();//item个数
		int left = 0;
		int top = 0;
		int right = 0;
		int bottom = 0;
		left = parent.getPaddingLeft() + mDividerPaddingLeft;
		right = parent.getWidth() - parent.getPaddingRight() - mDividerPaddingRight;
		boolean headerPosHandle = true;
		boolean footerPosHandle = true;
		boolean isSubDividerHandle = true;
		boolean isRedrawDividerHandle = true;
		for (int i = 0; i < childCount; i++) {
			final View child = parent.getChildAt(i);
			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
					.getLayoutParams();
			if (isStickyHeader) {
				RecyclerView.Adapter adapter = parent.getAdapter();
				if (adapter instanceof OnHeaderListener) {
					int adapterPosition = parent.getChildAdapterPosition(child);
					OnHeaderListener listener = ((OnHeaderListener) adapter);
					String headerName = listener.getHeaderName(adapterPosition);
					if (!TextUtils.isEmpty(headerName)) {
						if (adapterPosition != 0 && !headerName.equals(listener.getHeaderName(adapterPosition - 1))) {
							bottom = child.getTop() - params.topMargin;//计算分割线的下边
							top = bottom - mStickyHeaderHeight;//计算分割线的上边
//							top = child.getBottom() + params.bottomMargin;
//							bottom = top + mStickyHeaderHeight;
							if (mStickyHeaderDrawable != null) {
								mStickyHeaderDrawable.setBounds(left, top, right, bottom);
								mStickyHeaderDrawable.draw(c);
							} else {
								mDivider.setBounds(left, top, right, bottom);
								mDivider.draw(c);
							}
							int x = left + textPaddingLeft;
							float y = top+mStickyHeaderHeight / 2 + axis;
							c.drawText(headerName, x, y, mHeaderTextPaint);
//							return;
						}
					}
				}

			}
			//<editor-fold desc="顶部分割线绘制与定制">
			if (!noDrawHeaderDivider) {//顶部分割线处理
				if (headerPosHandle) {
					int adapterPosition = parent.getChildAdapterPosition(child);
					if (0 == adapterPosition) {
						headerPosHandle = false;
						Logd(TAG, "drawHorizontal: !noDrawHeaderDivider:" + i + ",adapterPosition:" + adapterPosition);
						bottom = child.getTop() - params.topMargin;
						if (isRedrawHeaderDivider) {//顶部分割线定制
							top = bottom - mHeaderDividerHeight;
							if (mHeaderDividerDrawable != null) {
								mHeaderDividerDrawable.setBounds(left, top, right, bottom);
								mHeaderDividerDrawable.draw(c);
							} else {
								mDivider.setBounds(left, top, right, bottom);
								mDivider.draw(c);
							}
						} else {
							top = bottom - mDividerHeight;
							mDivider.setBounds(left, top, right, bottom);
							mDivider.draw(c);
						}
					} else {
						headerPosHandle = false;
					}
				}
			}
			//</editor-fold>
			int bottomDividerHeight = mDividerHeight;
			//<editor-fold desc="底部分割线绘制与定制">
			if (noDrawFooterDivider) {//底部分割线处理
				if (footerPosHandle) {
					if (childCount - 1 == i) {
						int adapterPosition = parent.getChildAdapterPosition(child);
						if (itemCount - 1 == adapterPosition) {
							bottomDividerHeight = 0;
							footerPosHandle = false;
						} else {
							footerPosHandle = false;
						}
					}
				}
			} else {
				if (isRedrawFooterDivider) {//底部分割线定制
					if (footerPosHandle) {
						if (childCount - 1 == i) {
							int adapterPosition = parent.getChildAdapterPosition(child);
							if (itemCount - 1 == adapterPosition) {
								bottomDividerHeight = mFooterDividerHeight;
								footerPosHandle = false;
								if (mFooterDividerDrawable != null) {
									top = child.getBottom() + params.bottomMargin;
									bottom = top + bottomDividerHeight;
									mFooterDividerDrawable.setBounds(left, top, right, bottom);
									mFooterDividerDrawable.draw(c);
									continue;
								}
							} else {
								footerPosHandle = false;
							}
						}
					}
				}
			}
			//</editor-fold>
			//<editor-fold desc="分割线定制">
			if (isRedrawDivider) {//分割线的定制
				if (isRedrawDividerHandle) {
					int adapterPosition = parent.getChildAdapterPosition(child);
					if (Math.min(mDividerIndex, itemCount - 2) == adapterPosition) {
						isRedrawDividerHandle = false;
						top = child.getBottom() + params.bottomMargin;
						bottom = top + mRedrawDividerHeight;
						if (mDividerDrawable != null) {
							mDividerDrawable.setBounds(left, top, right, bottom);
							mDividerDrawable.draw(c);
						} else {
							mDivider.setBounds(left, top, right, bottom);
							mDivider.draw(c);
						}
						continue;
					}
				}
			}
			//</editor-fold>
			//<editor-fold desc="分割线批量定制">
			if (isSubDivider) {//分割线的截取
				if (isSubDividerHandle) {
					int adapterPosition = parent.getChildAdapterPosition(child);
					if (mStartIndex >= itemCount - 1) {
						isSubDivider = false;
					} else {
						if (adapterPosition >= mStartIndex) {
							if (adapterPosition < Math.min(mEndIndex, itemCount - 1)) {
								bottomDividerHeight = mSubDividerHeight;
								if (adapterPosition == mEndIndex - 1) {
									isSubDividerHandle = false;
								}
								if (mSubDrawable != null) {
									top = child.getBottom() + params.bottomMargin;
									bottom = top + bottomDividerHeight;
									mSubDrawable.setBounds(left, top, right, bottom);
									mSubDrawable.draw(c);
									continue;
								}
							} else {
								isSubDividerHandle = false;
							}
						}
					}
				}
			}
			//</editor-fold>
			top = child.getBottom() + params.bottomMargin;
			bottom = top + bottomDividerHeight;
			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}

	Map<Integer, TextView> map = new HashMap<>(8);

	@Override
	public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDrawOver(c, parent, state);
		if (isStickyHeader) {
			RecyclerView.Adapter adapter = parent.getAdapter();
			if (adapter instanceof OnHeaderListener) {
				OnHeaderListener listener = ((OnHeaderListener) adapter);
				final int childCount = parent.getChildCount();
				for (int i = 0; i < childCount; i++) {
					final View child = parent.getChildAt(i);
					int childAdapterPosition = parent.getChildAdapterPosition(child);
					Logd(TAG, "onDrawOver: childAdapterPosition:" + childAdapterPosition);
				}
				View child = parent.getChildAt(1);
				int adapterPosition = parent.getChildAdapterPosition(child);
				String headerName = listener.getHeaderName(adapterPosition);
				Loge(TAG, "onDrawOver: headerName:" + headerName + ",adapterPosition:" + adapterPosition);
				if (!TextUtils.isEmpty(headerName)) {
					int stickyTop = parent.getTop();
					int stickyBottom =/* parent.getPaddingTop() + */stickyTop + mStickyHeaderHeight;
					int left = parent.getPaddingLeft() + mDividerPaddingLeft;
					int right = parent.getWidth() - parent.getPaddingRight() - mDividerPaddingRight;
					if (adapterPosition != 0 && !headerName.equals(listener.getHeaderName(adapterPosition - 1))) {
						final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
								.getLayoutParams();
						int bottom = child.getTop() - params.topMargin;
						int top = bottom - mStickyHeaderHeight;
						Logd(TAG, "onDrawOver: stickyBottom:" + stickyBottom + ",stickyTop:" + stickyTop + ",top:" + top + ",bottom:" + bottom);
						if (stickyBottom >= top && top > stickyTop) {
							int dy = stickyBottom - top;
							bottom = top;
							top = top - mStickyHeaderHeight;
							if (mStickyHeaderDrawable != null) {
								mStickyHeaderDrawable.setBounds(left, top, right, bottom);
								mStickyHeaderDrawable.draw(c);
							} else {
								mDivider.setBounds(left, top, right, bottom);
								mDivider.draw(c);
							}
							int x = left + textPaddingLeft;
							float y = mStickyHeaderHeight / 2 + axis;
							String headerName1 = listener.getHeaderName(adapterPosition - 1);
							if (!TextUtils.isEmpty(headerName1)) {
								c.drawText(headerName1, x, -dy + y, mHeaderTextPaint);
							}
							Logd(TAG, "onDrawOver: >>>>" + x + ",y:" + y);
							return;
						}
					}
					if (mStickyHeaderDrawable != null) {
						mStickyHeaderDrawable.setBounds(left, stickyTop, right, stickyBottom);
						mStickyHeaderDrawable.draw(c);
					} else {
						mDivider.setBounds(left, stickyTop, right, stickyBottom);
						mDivider.draw(c);
					}
					int x = left + textPaddingLeft;
					float y = mStickyHeaderHeight / 2 + axis;
					String headerName1 = listener.getHeaderName(adapterPosition - 1);
					if (!TextUtils.isEmpty(headerName1)) {
						c.drawText(headerName1, x, y, mHeaderTextPaint);
						Logd(TAG, "onDrawOver: " + x + ",y:" + y + ",mStickyHeaderHeight:" + mStickyHeaderHeight);
					}
				}
			}
		}
	}

	/**
	 * 要想清楚outRect作用,请看{@link android.support.v7.widget.LinearLayoutManager}源码，如：measureChild().
	 */
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		int itemPosition = parent.getChildAdapterPosition(view);
		Loge(TAG, "getItemOffsets: itemPosition:" + itemPosition);
		if (mOrientation == VERTICAL) {
			int top = 0;
			int bottom = mDividerHeight;
			if (isStickyHeader) {//固定头部
				RecyclerView.Adapter adapter = parent.getAdapter();
				if (adapter instanceof OnHeaderListener) {
					OnHeaderListener listener = ((OnHeaderListener) adapter);
					String headerName = listener.getHeaderName(itemPosition);
					if (!TextUtils.isEmpty(headerName)) {
						if (itemPosition == 0 || !headerName.equals(listener.getHeaderName(itemPosition - 1))) {
							Logd(TAG, "getItemOffsets: headerName:" + headerName + ",itemPosition:" + itemPosition);
							top = mStickyHeaderHeight;
//							bottom = 0;
							outRect.set(0, top, 0, bottom);
							return;
						}
					}
				}
			}
			//<editor-fold desc="顶部分割线绘制与定制">
			if (!noDrawHeaderDivider) {
				if (itemPosition == 0) {
					if (isRedrawHeaderDivider) {
						top = mHeaderDividerHeight;
					} else {
						top = mDividerHeight;
					}
				}
			}
			//</editor-fold>
			//<editor-fold desc="底部分割线绘制与定制">
			if (noDrawFooterDivider) {
				int itemCount = parent.getAdapter().getItemCount();
				if (itemPosition == itemCount - 1) {
					bottom = 0;
				}
			} else {
				if (isRedrawFooterDivider) {
					int itemCount = parent.getAdapter().getItemCount();
					if (itemPosition == itemCount - 1) {
						Logd(TAG, "getItemOffsets: mFooterDividerHeight" + mFooterDividerHeight);
						bottom = mFooterDividerHeight;
					}
				}
			}
			//</editor-fold>
			//<editor-fold desc="分割线批量定制">
			if (isSubDivider) {
				int itemCount = parent.getAdapter().getItemCount();
				if (mStartIndex >= itemCount - 1) {
					isSubDivider = false;
				} else {
					if (itemPosition >= mStartIndex && itemPosition < Math.min(mEndIndex, itemCount - 1)) {
						Logd(TAG, "getItemOffsets: mStartIndex:" + mStartIndex + ",mEndIndex:" + mEndIndex + ",itemPosition:" + itemPosition);
						bottom = mSubDividerHeight;
					}
				}
			}
			//</editor-fold>
			//<editor-fold desc="分割线定制">
			if (isRedrawDivider) {
				int itemCount = parent.getAdapter().getItemCount();
				if (Math.min(mDividerIndex, itemCount - 2) == itemPosition) {
					bottom = mRedrawDividerHeight;
				}
			}
			//</editor-fold>
			outRect.set(0, top, 0, bottom);
		} else {
			int left = 0;
			int right = mDividerWidth;
			//<editor-fold desc="最左边分割线绘制与定制">
			if (!noDrawLeftDivider) {
				if (itemPosition == 0) {
					if (isRedrawLeftDivider) {
						left = mLeftDividerWidth;
					} else {
						left = mDividerWidth;
					}
				}
			}
			//</editor-fold>
			//<editor-fold desc="最右边分割线绘制与定制">
			if (noDrawRightDivider) {
				int itemCount = parent.getAdapter().getItemCount();
				if (itemPosition == itemCount - 1) {
					right = 0;
				}
			} else {
				if (isRedrawRightDivider) {
					int itemCount = parent.getAdapter().getItemCount();
					if (itemPosition == itemCount - 1) {
						right = mRightDividerWidth;
					}
				}
			}
			//</editor-fold>
			//<editor-fold desc="分割线批量定制">
			if (isSubDivider) {
				int itemCount = parent.getAdapter().getItemCount();
				if (mStartIndex >= itemCount - 1) {
					isSubDivider = false;
				} else {
					if (itemPosition >= mStartIndex && itemPosition < Math.min(mEndIndex, itemCount - 1)) {
						Logd(TAG, "getItemOffsets: mStartIndex:" + mStartIndex + ",mEndIndex:" + mEndIndex + ",itemPosition:" + itemPosition);
						right = mSubDividerWidth;
					}
				}
			}
			//</editor-fold>
			//<editor-fold desc="分割线定制">
			if (isRedrawDivider) {
				int itemCount = parent.getAdapter().getItemCount();
				if (Math.min(mDividerIndex, itemCount - 2) == itemPosition) {
					right = mRedrawDividerWidth;
				}
			}
			//</editor-fold>
			outRect.set(left, 0, right, 0);
		}
	}

	public static class Builder extends BaseBuilder {
		private int mDividerPaddingLeft;//分割线左边距，仅适用于竖直方向
		private int mDividerPaddingTop;//分割线上边距，仅适用于水平方向
		private int mDividerPaddingRight;//分割线右边距，仅适用于竖直方向
		private int mDividerPaddingBottom;//分割线下边距，仅适用于水平方向
		private boolean isStickyHeader;//固定头部
		private int mStickyHeaderHeight;//固定头部高度
		private Drawable mStickyHeaderDrawable;//固定头部样式

		public Builder(Context context) {
			super(context, LINEAR);
		}

		public Builder(Context context, int mOrientation) {
			super(context, LINEAR, mOrientation);
		}

		public Builder setDividerPaddingLeft(@IntRange(from = 0) int paddingLeft) {
			this.mDividerPaddingLeft = paddingLeft;
			return this;
		}

		public Builder setDividerPaddingRight(@IntRange(from = 0) int paddingRight) {
			this.mDividerPaddingRight = paddingRight;
			return this;
		}

		public Builder setDividerPaddingTop(@IntRange(from = 0) int paddingTop) {
			this.mDividerPaddingTop = paddingTop;
			return this;
		}

		public Builder setDividerPaddingBottom(@IntRange(from = 0) int paddingBottom) {
			this.mDividerPaddingBottom = paddingBottom;
			return this;
		}

		public Builder setDividerPadding(@IntRange(from = 0) int... padding) {
			for (int i = 0; i < padding.length; i++) {
				if (i == 0) {
					this.mDividerPaddingLeft = padding[i];
				} else if (i == 1) {
					this.mDividerPaddingTop = padding[i];
				} else if (i == 2) {
					this.mDividerPaddingRight = padding[i];
				} else if (i == 3) {
					this.mDividerPaddingBottom = padding[i];
					break;
				}
			}
			return this;
		}

		public Builder setStickyHeader(boolean stickyHeader) {
			isStickyHeader = stickyHeader;
			return this;
		}

		public Builder setStickyHeaderHeight(@IntRange(from = 0) int stickyHeaderHeight) {
			this.mStickyHeaderHeight = stickyHeaderHeight;
			return this;
		}

		public Builder setStickyHeaderDrawable(@DrawableRes int drawableId) {
			this.mStickyHeaderDrawable = ContextCompat.getDrawable(mContext.getApplicationContext(), drawableId);
			;
			if (this.mStickyHeaderHeight == 0) {
				this.mStickyHeaderHeight = mStickyHeaderDrawable.getIntrinsicHeight();
			}
			return this;
		}

		public DividerLinearItemDecoration build() {
			return new DividerLinearItemDecoration(this);
		}
	}
}
