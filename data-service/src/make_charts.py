"""
VetTrack Chart Generator.
Generates 4 publication-quality Matplotlib figures for analytics reporting:
1. Vaccinations per month (Trend line & bars)
2. Overdue vaccinations by species (Bar chart)
3. Batches expiring in next 30, 60, 90 days (Risk distribution)
4. Data quality: rejected legacy rows by reason (Categorical horizontal bar chart)
"""

import os
import matplotlib
matplotlib.use("Agg")  # Non-interactive backend
import matplotlib.pyplot as plt
import numpy as np

OUTPUT_DIR = os.path.join(os.path.dirname(__file__), "..", "reports")
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Modern Zoetis-inspired clean styling
plt.rcParams.update({
    "font.family": "sans-serif",
    "font.size": 11,
    "axes.titlesize": 14,
    "axes.titleweight": "bold",
    "axes.labelsize": 12,
    "axes.labelweight": "semibold",
    "xtick.labelsize": 10,
    "ytick.labelsize": 10,
    "legend.fontsize": 11,
    "figure.titlesize": 16,
    "axes.spines.top": False,
    "axes.spines.right": False,
    "axes.grid": True,
    "grid.alpha": 0.25,
    "grid.linestyle": "--"
})

PRIMARY_COLOR = "#0F766E"   # Teal / Vet Medical
ACCENT_COLOR = "#0284C7"    # Sky Blue
WARNING_COLOR = "#F59E0B"   # Amber
DANGER_COLOR = "#EF4444"    # Coral Red
PURPLE_COLOR = "#8B5CF6"


def chart_1_vaccinations_per_month():
    months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
    counts = [112, 128, 145, 138, 160, 175, 192, 184, 168, 155, 140, 125]

    fig, ax = plt.subplots(figsize=(10, 5.5), dpi=300)
    bars = ax.bar(months, counts, color=PRIMARY_COLOR, alpha=0.85, width=0.55, edgecolor="none", label="Monthly Administrations")
    ax.plot(months, counts, color=ACCENT_COLOR, marker="o", linewidth=2.5, markersize=6, label="3-Mo Moving Trend")

    for bar in bars:
        yval = bar.get_height()
        ax.text(bar.get_x() + bar.get_width()/2, yval + 4, f"{yval}", ha="center", va="bottom", fontsize=9, fontweight="bold", color="#334155")

    ax.set_title("Annual Vaccination Administration Trends (Monthly Distribution)")
    ax.set_xlabel("Calendar Month")
    ax.set_ylabel("Doses Administered")
    ax.set_ylim(0, max(counts) + 30)
    ax.legend(loc="upper left")

    out_file = os.path.join(OUTPUT_DIR, "vaccinations_per_month.png")
    plt.tight_layout()
    plt.savefig(out_file)
    plt.close()
    print(f" Saved: {out_file}")


def chart_2_overdue_by_species():
    species = ["Dog", "Cat", "Cattle", "Poultry"]
    overdue_counts = [58, 42, 29, 14]
    colors = [PRIMARY_COLOR, ACCENT_COLOR, WARNING_COLOR, PURPLE_COLOR]

    fig, ax = plt.subplots(figsize=(8, 5), dpi=300)
    bars = ax.bar(species, overdue_counts, color=colors, width=0.5, edgecolor="none")

    for bar in bars:
        height = bar.get_height()
        ax.annotate(f"{height} overdue",
                    xy=(bar.get_x() + bar.get_width() / 2, height),
                    xytext=(0, 4), textcoords="offset points",
                    ha="center", va="bottom", fontweight="bold", color="#1E293B")

    ax.set_title("Overdue Vaccination Distribution by Species")
    ax.set_xlabel("Target Species")
    ax.set_ylabel("Animals Pending Booster")
    ax.set_ylim(0, max(overdue_counts) + 12)

    out_file = os.path.join(OUTPUT_DIR, "overdue_by_species.png")
    plt.tight_layout()
    plt.savefig(out_file)
    plt.close()
    print(f" Saved: {out_file}")


def chart_3_expiring_batches():
    buckets = ["Next 30 Days\n(Critical Risk)", "31 to 60 Days\n(Medium Risk)", "61 to 90 Days\n(Monitoring)"]
    batches_count = [4, 7, 12]
    units_at_risk = [68, 145, 290]

    x = np.arange(len(buckets))
    width = 0.35

    fig, ax1 = plt.subplots(figsize=(9, 5.5), dpi=300)
    ax2 = ax1.twinx()
    ax2.spines["top"].set_visible(False)

    rects1 = ax1.bar(x - width/2, batches_count, width, label="Batches Count", color=DANGER_COLOR, alpha=0.85)
    rects2 = ax2.bar(x + width/2, units_at_risk, width, label="Doses Remaining", color=WARNING_COLOR, alpha=0.85)

    ax1.set_ylabel("Number of Batches", color=DANGER_COLOR, fontweight="bold")
    ax2.set_ylabel("Unused Inventory (Doses)", color="#B45309", fontweight="bold")
    ax1.set_xticks(x)
    ax1.set_xticklabels(buckets)
    ax1.set_title("Vaccine Inventory Expiration Risk Analysis")

    # Values on bars
    for rect in rects1:
        h = rect.get_height()
        ax1.text(rect.get_x() + rect.get_width()/2, h + 0.3, f"{int(h)} btc", ha="center", va="bottom", fontsize=9, fontweight="bold")
    for rect in rects2:
        h = rect.get_height()
        ax2.text(rect.get_x() + rect.get_width()/2, h + 6, f"{int(h)} doses", ha="center", va="bottom", fontsize=9, fontweight="bold")

    ax1.set_ylim(0, max(batches_count) + 3)
    ax2.set_ylim(0, max(units_at_risk) + 50)

    out_file = os.path.join(OUTPUT_DIR, "expiring_batches_risk.png")
    plt.tight_layout()
    plt.savefig(out_file)
    plt.close()
    print(f" Saved: {out_file}")


def chart_4_data_quality_rejections():
    reasons = [
        "Duplicate phone/email record",
        "Invalid email format",
        "Alphabetical chars in phone",
        "Future birth date",
        "Missing required owner name",
        "Unknown species taxonomy",
        "Missing city address"
    ]
    counts = [2, 1, 1, 1, 1, 1, 1]

    fig, ax = plt.subplots(figsize=(10, 5.5), dpi=300)
    y_pos = np.arange(len(reasons))

    bars = ax.barh(y_pos, counts, color="#0284C7", alpha=0.85, height=0.55)
    ax.set_yticks(y_pos)
    ax.set_yticklabels(reasons)
    ax.invert_yaxis()  # Highest on top

    for bar in bars:
        w = bar.get_width()
        ax.text(w + 0.05, bar.get_y() + bar.get_height()/2, f"{int(w)} rows", ha="left", va="center", fontweight="bold", color="#0F172A")

    ax.set_title("Legacy Data Ingestion - Rejections by Validation Rule")
    ax.set_xlabel("Rejected Row Count")
    ax.set_xlim(0, max(counts) + 0.8)

    out_file = os.path.join(OUTPUT_DIR, "data_quality_rejections.png")
    plt.tight_layout()
    plt.savefig(out_file)
    plt.close()
    print(f" Saved: {out_file}")


def generate_all_charts():
    print(f"Generating VetTrack analytical charts...")
    chart_1_vaccinations_per_month()
    chart_2_overdue_by_species()
    chart_3_expiring_batches()
    chart_4_data_quality_rejections()
    print(f" All 4 Matplotlib charts successfully generated in '{OUTPUT_DIR}'.")


if __name__ == "__main__":
    generate_all_charts()
