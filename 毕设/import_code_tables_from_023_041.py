# import pandas as pd
# import re
# from sqlalchemy import create_engine, text

# # ====================== 数据库配置 ======================
# DB_USER = 'root'
# DB_PASSWORD = '0421'      # 改成你的密码
# DB_HOST = 'localhost'
# DB_NAME = 'standard_db'

# engine = create_engine(f'mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}?charset=utf8mb4')

# # ====================== 辅助函数 ======================
# def get_standard_doc_id(std_code, std_name):
#     """获取标准文档ID，不存在则插入"""
#     with engine.connect() as conn:
#         result = conn.execute(
#             text("SELECT id FROM standard_document WHERE code = :code"),
#             {"code": std_code}
#         ).fetchone()
#         if result:
#             return result[0]
#         else:
#             conn.execute(
#                 text("INSERT INTO standard_document (code, name) VALUES (:code, :name)"),
#                 {"code": std_code, "name": std_name}
#             )
#             conn.commit()
#             return conn.execute(
#                 text("SELECT id FROM standard_document WHERE code = :code"),
#                 {"code": std_code}
#             ).fetchone()[0]

# def insert_code_table(name, code, doc_id):
#     """插入代码表，返回id"""
#     with engine.connect() as conn:
#         # 检查是否已存在相同code和doc_id的记录
#         res = conn.execute(
#             text("SELECT id FROM code_table WHERE code = :code AND standard_doc_id = :doc_id"),
#             {"code": code, "doc_id": doc_id}
#         ).fetchone()
#         if res:
#             return res[0]
#         conn.execute(
#             text("INSERT INTO code_table (name, code, standard_doc_id) VALUES (:name, :code, :doc_id)"),
#             {"name": name, "code": code, "doc_id": doc_id}
#         )
#         conn.commit()
#         return conn.execute(
#             text("SELECT id FROM code_table WHERE code = :code AND standard_doc_id = :doc_id"),
#             {"code": code, "doc_id": doc_id}
#         ).fetchone()[0]

# def insert_code_item(table_id, code_value, meaning, parent_id=0, sort_order=0):
#     with engine.connect() as conn:
#         conn.execute(
#             text("INSERT INTO code_item (code_table_id, code, value, parent_id, sort_order) VALUES (:tid, :code, :val, :pid, :sort)"),
#             {"tid": table_id, "code": code_value, "val": meaning, "pid": parent_id, "sort": sort_order}
#         )
#         conn.commit()

# # ====================== 解析 Excel 中的代码表 ======================
# def parse_code_tables_from_excel(file_path, std_code, std_name):
#     print(f"正在处理: {file_path}")
#     df = pd.read_excel(file_path, engine='openpyxl')
#     # 列名可能是 ['核心分类', '具体条目', '详细内容'] 或类似
#     # 兼容不同文件：检查列名
#     cat_col = None
#     item_col = None
#     cont_col = None
#     for col in df.columns:
#         if '分类' in col:
#             cat_col = col
#         elif '条目' in col or '具体项目' in col:
#             item_col = col
#         elif '详细' in col or '内容' in col:
#             cont_col = col
#     if cat_col is None or item_col is None or cont_col is None:
#         print("  ❌ 无法识别列名，跳过")
#         return

#     # 筛选出“数据元值域代码表”相关的行
#     mask = df[cat_col].astype(str).str.contains('数据元值域代码表', na=False)
#     code_rows = df[mask].copy()
#     if code_rows.empty:
#         print("  ⚠️ 未找到“数据元值域代码表”部分")
#         return

#     doc_id = get_standard_doc_id(std_code, std_name)

#     # 遍历这些行，解析每个代码表
#     current_table_id = None
#     current_table_code = None
#     current_table_name = None

#     for idx, row in code_rows.iterrows():
#         item = str(row[item_col]) if pd.notna(row[item_col]) else ''
#         content = str(row[cont_col]) if pd.notna(row[cont_col]) else ''

#         # 判断是否是代码表的标题行（如 "中药储存方法代码（CV08.50.A12）"）
#         if '代码表' in item and ('（' in item or '(' in item):
#             # 提取名称和标识符
#             match = re.search(r'(.+?)[（(]([^（）]+)[）)]', item)
#             if match:
#                 table_name = match.group(1).strip()
#                 table_code = match.group(2).strip()
#             else:
#                 # 没有括号的情况，直接用整个 item 作为名称，默认 code 为空
#                 table_name = item.strip()
#                 table_code = item.strip().replace('代码表', '').strip()
#             # 插入代码表
#             current_table_id = insert_code_table(table_name, table_code, doc_id)
#             current_table_name = table_name
#             current_table_code = table_code
#             print(f"  ✅ 发现代码表: {table_name} ({table_code})")
#         elif current_table_id is not None:
#             # 当前行可能是代码项，例如 "1. 常温储存" 或 "1  常温储存"
#             # 也可能是多行描述，但根据实际数据，代码项格式为 "数字. 含义"
#             # 也可能内容列有详细说明，我们优先使用 item 列提取
#             text_to_parse = item if item and item != 'nan' else content
#             if text_to_parse and text_to_parse != 'nan':
#                 # 匹配类似 "1. 常温储存" 或 "1 常温储存"
#                 match_item = re.match(r'^(\d+(?:\.\d+)?)\s*[.、]?\s*(.*)', text_to_parse)
#                 if match_item:
#                     code_val = match_item.group(1)
#                     meaning = match_item.group(2).strip()
#                     if meaning:
#                         insert_code_item(current_table_id, code_val, meaning)
#                         # print(f"    插入代码项: {code_val} -> {meaning}")
#                 else:
#                     # 可能是多行文本中的一行，忽略
#                     pass
#         # 如果当前行不是标题，且没有当前表，则跳过

#     print(f"  ✅ 完成 {std_code} 的代码表导入")

# # ====================== 主程序 ======================
# if __name__ == '__main__':
#     # 文件路径（请根据实际位置修改）
#     file_023 = r'C:\Users\86158\Desktop\23 T CIATCM 023-2019.xlsx'
#     file_041 = r'C:\Users\86158\Desktop\41 T CIATCM 041-2019.xlsx'

#     parse_code_tables_from_excel(file_023, 'T/CIATCM 023-2019', '中药煎药管理与质量控制信息基本数据集')
#     parse_code_tables_from_excel(file_041, 'T/CIATCM 041-2019', '基层医疗卫生机构中医诊疗区(中医馆)电子病历基本数据集')

#     print("所有代码表导入完成！")




# import re
# import pymysql
# from collections import defaultdict

# # 数据库配置
# DB_CONFIG = {
#     'host': 'localhost',
#     'user': 'root',
#     'password': '0421',    # 改成你的密码
#     'database': 'standard_db',
#     'charset': 'utf8mb4'
# }

# conn = pymysql.connect(**DB_CONFIG)
# cursor = conn.cursor()

# # 1. 获取所有术语
# cursor.execute("SELECT id, name, definition FROM term")
# terms = cursor.fetchall()

# # 构建术语名称集合（用于快速匹配），同时保留 id 映射
# term_names = {}
# for term_id, name, defn in terms:
#     # 去除名称中的空格、括号等干扰，但保留原始名称用于匹配
#     term_names[name] = term_id
#     # 也会匹配定义中可能出现的别名？简单起见只匹配完整名称

# # 为了避免重复，用 set 存储已经插入的关系对
# inserted_pairs = set()

# # 2. 遍历每个术语的定义，查找其他术语
# for source_id, source_name, definition in terms:
#     if not definition:
#         continue
#     # 在定义中查找出现的其他术语名称
#     for target_name, target_id in term_names.items():
#         if source_id == target_id:
#             continue  # 避免自己关联自己
#         # 使用正则确保匹配独立词汇（避免部分匹配，如“数据”匹配到“数据库”）
#         # 简单起见，可以用 re.search(r'\b' + re.escape(target_name) + r'\b', definition)
#         pattern = r'(?<![a-zA-Z0-9\u4e00-\u9fa5])' + re.escape(target_name) + r'(?![a-zA-Z0-9\u4e00-\u9fa5])'
#         if re.search(pattern, definition):
#             pair = (source_id, target_id)
#             if pair not in inserted_pairs:
#                 inserted_pairs.add(pair)
#                 try:
#                     cursor.execute(
#                         "INSERT INTO term_relation (source_term_id, target_term_id, relation_type) VALUES (%s, %s, %s)",
#                         (source_id, target_id, '相关')
#                     )
#                     print(f"插入关系: {source_name} -> {target_name}")
#                 except Exception as e:
#                     print(f"插入失败 {source_name}->{target_name}: {e}")

# conn.commit()
# cursor.close()
# conn.close()
# print("完成！共插入 %d 条关系" % len(inserted_pairs))


# import re
# import pymysql

# # 数据库配置
# DB_CONFIG = {
#     'host': 'localhost',
#     'user': 'root',
#     'password': '0421',
#     'database': 'standard_db',
#     'charset': 'utf8mb4'
# }

# # 只处理 source_standard = 'T/CIATCM 001-2019'
# TARGET_STANDARD = 'T/CIATCM 001-2019'

# def main():
#     conn = pymysql.connect(**DB_CONFIG)
#     cursor = conn.cursor()

#     # 1. 获取该标准下的所有术语
#     cursor.execute("""
#         SELECT id, name, definition
#         FROM term
#         WHERE source_standard = %s
#     """, (TARGET_STANDARD,))
#     terms = cursor.fetchall()

#     # 构建术语名称到id的映射
#     term_map = {name: tid for tid, name, _ in terms}
#     # 注意：如果名称有重复，后出现的会覆盖，但实际名称应唯一

#     inserted = 0
#     for source_id, source_name, definition in terms:
#         if not definition:
#             continue
#         # 在定义中查找其他术语名称
#         for target_name, target_id in term_map.items():
#             if source_id == target_id:
#                 continue
#             # 使用正则避免部分匹配（如“数据”匹配“数据库”）
#             pattern = r'(?<![a-zA-Z0-9\u4e00-\u9fa5])' + re.escape(target_name) + r'(?![a-zA-Z0-9\u4e00-\u9fa5])'
#             if re.search(pattern, definition):
#                 try:
#                     cursor.execute("""
#                         INSERT IGNORE INTO term_relation (source_term_id, target_term_id, relation_type)
#                         VALUES (%s, %s, '相关')
#                     """, (source_id, target_id))
#                     if cursor.rowcount > 0:
#                         inserted += 1
#                         print(f"插入关系: {source_name} -> {target_name}")
#                 except Exception as e:
#                     print(f"插入失败 {source_name}->{target_name}: {e}")

#     conn.commit()
#     cursor.close()
#     conn.close()
#     print(f"完成！共插入 {inserted} 条关系（来源标准: {TARGET_STANDARD}）")

# if __name__ == '__main__':
#     main()




import re
import pymysql
from fuzzywuzzy import fuzz
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np

DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '0421',
    'database': 'standard_db',
    'charset': 'utf8mb4'
}

# 参数配置
TOP_K_PER_TERM = 3          # 每个术语最多连接的同组其他术语数
SIM_THRESHOLD = 0.4         # 相似度阈值（低于此不建关系）
NAME_WEIGHT = 0.3
DEF_WEIGHT = 0.7

def preprocess_text(text):
    if not text:
        return ''
    text = re.sub(r'[^\u4e00-\u9fa5a-zA-Z0-9]+', ' ', text)
    return text.strip().lower()

def process_group(cursor, group_id_list, group_names, group_defs, group_category, group_standard):
    """处理一个 (source_standard, category) 小组内的所有术语"""
    n = len(group_id_list)
    if n <= 1:
        return 0
    # 1. 计算名称相似度矩阵
    name_sim = np.zeros((n, n))
    for i in range(n):
        for j in range(i+1, n):
            sim = fuzz.partial_ratio(group_names[i], group_names[j]) / 100.0
            name_sim[i, j] = sim
            name_sim[j, i] = sim

    # 2. 计算定义TF-IDF相似度
    cleaned = [preprocess_text(d) for d in group_defs]
    vectorizer = TfidfVectorizer(max_features=500)
    tfidf = vectorizer.fit_transform(cleaned)
    def_sim = cosine_similarity(tfidf)

    # 3. 综合相似度
    combined = NAME_WEIGHT * name_sim + DEF_WEIGHT * def_sim

    # 4. 为每个术语生成关系
    inserted = 0
    for i in range(n):
        source_id = group_id_list[i]
        source_name = group_names[i]
        sims = sorted([(j, combined[i][j]) for j in range(n) if j != i], key=lambda x: x[1], reverse=True)
        count = 0
        for j, score in sims:
            if score < SIM_THRESHOLD:
                break
            if count >= TOP_K_PER_TERM:
                break
            target_id = group_id_list[j]
            try:
                cursor.execute("""
                    INSERT IGNORE INTO term_relation (source_term_id, target_term_id, relation_type)
                    VALUES (%s, %s, '')
                """, (source_id, target_id))
                if cursor.rowcount > 0:
                    inserted += 1
                    print(f"[{group_standard}][{group_category}] {source_name} -> {group_names[j]} (相似度={score:.3f})")
                count += 1
            except Exception as e:
                print(f"插入失败: {e}")
    return inserted

def main():
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    # 获取所有需要处理的分组 (source_standard, category)
    cursor.execute("""
        SELECT source_standard, category
        FROM term
        WHERE source_standard IS NOT NULL AND source_standard != ''
          AND category IS NOT NULL AND category != ''
          AND definition IS NOT NULL AND definition != ''
        GROUP BY source_standard, category
    """)
    groups = cursor.fetchall()

    total_inserted = 0
    for std, cat in groups:
        print(f"\n正在处理: 标准={std}, 分类={cat}")
        cursor.execute("""
            SELECT id, name, definition
            FROM term
            WHERE source_standard = %s AND category = %s
              AND definition IS NOT NULL AND definition != ''
        """, (std, cat))
        rows = cursor.fetchall()
        if len(rows) <= 1:
            print(f"  小组只有 {len(rows)} 个术语，跳过")
            continue
        ids = [r[0] for r in rows]
        names = [r[1] for r in rows]
        defs = [r[2] for r in rows]
        cnt = process_group(cursor, ids, names, defs, cat, std)
        total_inserted += cnt
        print(f"  本组插入 {cnt} 条关系")

    conn.commit()
    cursor.close()
    conn.close()
    print(f"\n全部完成！总共插入 {total_inserted} 条关系。")

if __name__ == '__main__':
    main()