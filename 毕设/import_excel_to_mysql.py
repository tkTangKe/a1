# import pandas as pd
# from sqlalchemy import create_engine, text
# import re

# # ====================== 数据库连接配置 ======================
# DB_USER = 'root'
# DB_PASSWORD = '0421'   # 改成你的密码
# DB_HOST = 'localhost'
# DB_NAME = 'standard_db'   # 数据库名

# engine = create_engine(f'mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}?charset=utf8mb4')

# # ====================== 辅助函数 ======================
# def insert_standard_document(std_code, std_name):
#     """插入或获取标准文档ID"""
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

# def insert_standard_content(doc_id, category, item_name, content):
#     with engine.connect() as conn:
#         conn.execute(
#             text("INSERT INTO standard_content (standard_doc_id, category, item_name, content) VALUES (:doc_id, :cat, :item, :cont)"),
#             {"doc_id": doc_id, "cat": category, "item": item_name, "cont": content}
#         )
#         conn.commit()

# # 解析术语名称（如 "3.1.1 中医药信息（...）" -> "中医药信息"）
# def parse_term_name(item):
#     if pd.isna(item):
#         return ''
#     s = str(item)
#     # 提取编号后的第一个词
#     match = re.search(r'\d+\.\d+(?:\.\d+)?\s+([^（(]+)', s)
#     if match:
#         return match.group(1).strip()
#     # 如果没有编号，直接取第一个空格前的内容
#     parts = s.split()
#     if parts:
#         return parts[0]
#     return s

# # 从分类字符串中提取术语的分类（如 "术语和定义 - 基本术语 - 一般术语" -> "一般术语"）
# def get_term_category(category_str):
#     if pd.isna(category_str):
#         return ''
#     if '基础设施' in category_str:
#         return '基础设施'
#     if '数据资源' in category_str:
#         return '数据资源'
#     if '应用系统' in category_str:
#         return '应用系统'
#     if '支撑体系' in category_str:
#         return '支撑体系'
#     if '一般术语' in category_str:
#         return '基础'
#     if '数据类术语' in category_str:
#         return '基础'
#     return '其他'

# # ====================== 处理 T/CIATCM 001-2019（三列格式） ======================
# def import_std001(file_path):
#     print("正在导入 T/CIATCM 001-2019 ...")
#     df = pd.read_excel(file_path, engine='openpyxl')
#     # 列名：核心内容分类, 具体项目, 详细要求
#     doc_id = insert_standard_document('T/CIATCM 001-2019', '中医药信息化常用术语')

#     for idx, row in df.iterrows():
#         cat = str(row['核心内容分类']) if pd.notna(row['核心内容分类']) else ''
#         item = str(row['具体项目']) if pd.notna(row['具体项目']) else ''
#         content = str(row['详细要求']) if pd.notna(row['详细要求']) else ''

#         # 判断是否为术语
#         if '术语和定义' in cat:
#             # 插入 term 表
#             term_name = parse_term_name(item)
#             if term_name and content and content != 'nan':
#                 term_cat = get_term_category(cat)
#                 with engine.connect() as conn:
#                     conn.execute(
#                         text("INSERT INTO term (name, definition, category, source_standard, standard_doc_id) VALUES (:name, :def, :cat, :src, :doc_id)"),
#                         {"name": term_name, "def": content, "cat": term_cat, "src": 'T/CIATCM 001-2019', "doc_id": doc_id}
#                     )
#                     conn.commit()
#         else:
#             # 其他内容存入 standard_content
#             if cat and cat != 'nan' and item and item != 'nan':
#                 insert_standard_content(doc_id, cat, item, content)

# # ====================== 处理 T/CIATCM 023-2019 和 041-2019（三列格式，列名可能略有不同） ======================
# def import_std_general(file_path, std_code, std_name):
#     print(f"正在导入 {std_code} ...")
#     df = pd.read_excel(file_path, engine='openpyxl')
#     # 这两个文件的列名是：核心分类, 具体条目, 详细内容
#     doc_id = insert_standard_document(std_code, std_name)

#     for idx, row in df.iterrows():
#         cat = str(row['核心分类']) if pd.notna(row['核心分类']) else ''
#         item = str(row['具体条目']) if pd.notna(row['具体条目']) else ''
#         content = str(row['详细内容']) if pd.notna(row['详细内容']) else ''

#         # 判断是否为术语（“术语和定义”出现在分类中）
#         if '术语和定义' in cat:
#             term_name = parse_term_name(item)
#             if term_name and content and content != 'nan':
#                 term_cat = get_term_category(cat)
#                 with engine.connect() as conn:
#                     conn.execute(
#                         text("INSERT INTO term (name, definition, category, source_standard, standard_doc_id) VALUES (:name, :def, :cat, :src, :doc_id)"),
#                         {"name": term_name, "def": content, "cat": term_cat, "src": std_code, "doc_id": doc_id}
#                     )
#                     conn.commit()
#         else:
#             if cat and cat != 'nan' and item and item != 'nan':
#                 insert_standard_content(doc_id, cat, item, content)

# # ====================== 主程序 ======================
# if __name__ == '__main__':
#     # 请根据你的实际文件路径修改
#     import_std001(r'C:\Users\86158\Desktop\1 T CIATCM 001-2019.xlsx')
#     import_std_general(r'C:\Users\86158\Desktop\23 T CIATCM 023-2019.xlsx', 'T/CIATCM 023-2019', '中药煎药管理与质量控制信息基本数据集')
#     import_std_general(r'C:\Users\86158\Desktop\41 T CIATCM 041-2019.xlsx', 'T/CIATCM 041-2019', '基层医疗卫生机构中医诊疗区(中医馆)电子病历基本数据集')
#     print("所有数据导入完成！")




# import pandas as pd
# from sqlalchemy import create_engine, text
# import re

# # ====================== 数据库连接配置 ======================
# DB_USER = 'root'
# DB_PASSWORD = '0421'   # 改成你的密码
# DB_HOST = 'localhost'
# DB_NAME = 'standard_db'   # 数据库名

# engine = create_engine(f'mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}?charset=utf8mb4')

# # ====================== 辅助函数 ======================
# def insert_standard_document(std_code, std_name):
#     """插入或获取标准文档ID"""
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

# def insert_standard_content(doc_id, category, item_name, content):
#     with engine.connect() as conn:
#         conn.execute(
#             text("INSERT INTO standard_content (standard_doc_id, category, item_name, content) VALUES (:doc_id, :cat, :item, :cont)"),
#             {"doc_id": doc_id, "cat": category, "item": item_name, "cont": content}
#         )
#         conn.commit()

# def parse_term_name(item):
#     """从带编号的术语项中提取纯名称，如 '3.1 张仲景经方（...）' → '张仲景经方'"""
#     if pd.isna(item):
#         return ''
#     s = str(item)
#     # 匹配类似 "3.1 标题" 或 "3.1.1 标题"
#     match = re.search(r'\d+(?:\.\d+)*\s+([^（(]+)', s)
#     if match:
#         return match.group(1).strip()
#     # 无编号时取第一个空格前的内容
#     if ' ' in s:
#         return s.split()[0]
#     return s

# def get_term_category(category_str):
#     """根据分类字符串确定术语的所属大类（用于 term 表的 category 字段）"""
#     if pd.isna(category_str):
#         return ''
#     if '基础设施' in category_str:
#         return '基础设施'
#     if '数据资源' in category_str:
#         return '数据资源'
#     if '应用系统' in category_str:
#         return '应用系统'
#     if '支撑体系' in category_str:
#         return '支撑体系'
#     # 太阳病篇、阳明病篇等属于术语分类
#     if '术语和定义' in category_str:
#         return '基础'
#     return '其他'

# # ====================== 通用导入函数（适用于三列格式：核心内容分类、具体项目、详细要求） ======================
# def import_std_unified(file_path, std_code, std_name):
#     print(f"正在导入 {std_code} ...")
#     df = pd.read_excel(file_path, engine='openpyxl')
#     # 列名确认
#     expected_cols = ['核心内容分类', '具体项目', '详细要求']
#     if not all(col in df.columns for col in expected_cols):
#         raise ValueError(f"文件 {file_path} 的列名不是 {expected_cols}，实际为 {list(df.columns)}")
    
#     doc_id = insert_standard_document(std_code, std_name)

#     for idx, row in df.iterrows():
#         category = row['核心内容分类']
#         item = row['具体项目']
#         content = row['详细要求']
        
#         # 跳过全空行
#         if pd.isna(category) and pd.isna(item) and pd.isna(content):
#             continue
        
#         cat_str = str(category) if pd.notna(category) else ''
#         item_str = str(item) if pd.notna(item) else ''
#         content_str = str(content) if pd.notna(content) else ''
        
#         # 判断是否为术语条目（分类中包含“术语和定义”）
#         if '术语和定义' in cat_str:
#             term_name = parse_term_name(item_str)
#             if term_name and content_str and content_str != 'nan':
#                 term_cat = get_term_category(cat_str)
#                 with engine.connect() as conn:
#                     conn.execute(
#                         text("INSERT INTO term (name, definition, category, source_standard, standard_doc_id) VALUES (:name, :def, :cat, :src, :doc_id)"),
#                         {"name": term_name, "def": content_str, "cat": term_cat, "src": std_code, "doc_id": doc_id}
#                     )
#                     conn.commit()
#         else:
#             # 其他非术语条目，存入 standard_content
#             if cat_str and cat_str != 'nan' and item_str and item_str != 'nan':
#                 insert_standard_content(doc_id, cat_str, item_str, content_str)

# # ====================== 主程序 ======================
# if __name__ == '__main__':
#     # 请根据你的实际文件路径修改
#     import_std_unified(r'C:\Users\86158\Desktop\T CIATCM 008-2019.xlsx', 'T/CIATCM 008-2019', '中医药卫生经济信息标准体系表')
#     import_std_unified(r'C:\Users\86158\Desktop\T CIATCM 107-2024.xlsx', 'T/CIATCM 107-2024', '张仲景经方传承基本名词术语')
#     import_std_unified(r'C:\Users\86158\Desktop\T CIATCM 119-2024.xlsx', 'T/CIATCM 119-2024', '数字中医药古籍标引规则')
#     print("所有数据导入完成！")



# import pandas as pd
# from sqlalchemy import create_engine, text
# import re

# # ====================== 数据库连接配置 ======================
# DB_USER = 'root'
# DB_PASSWORD = '0421'   # 改成你的密码
# DB_HOST = 'localhost'
# DB_NAME = 'standard_db'   # 数据库名

# engine = create_engine(f'mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}?charset=utf8mb4')

# # ====================== 辅助函数 ======================
# def insert_standard_document(std_code, std_name):
#     """插入或获取标准文档ID"""
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

# def insert_standard_content(doc_id, category, item_name, content):
#     with engine.connect() as conn:
#         conn.execute(
#             text("INSERT INTO standard_content (standard_doc_id, category, item_name, content) VALUES (:doc_id, :cat, :item, :cont)"),
#             {"doc_id": doc_id, "cat": category, "item": item_name, "cont": content}
#         )
#         conn.commit()

# def parse_term_name(item):
#     """从带编号的术语项中提取纯名称，如 '3.1 张仲景经方（...）' → '张仲景经方'"""
#     if pd.isna(item):
#         return ''
#     s = str(item)
#     match = re.search(r'\d+(?:\.\d+)*\s+([^（(]+)', s)
#     if match:
#         return match.group(1).strip()
#     if ' ' in s:
#         return s.split()[0]
#     return s

# def get_term_category(category_str):
#     """根据分类字符串确定术语的所属大类"""
#     if pd.isna(category_str):
#         return ''
#     if '基础设施' in category_str:
#         return '基础设施'
#     if '数据资源' in category_str:
#         return '数据资源'
#     if '应用系统' in category_str:
#         return '应用系统'
#     if '支撑体系' in category_str:
#         return '支撑体系'
#     if '术语和定义' in category_str:
#         return '基础'
#     return '其他'

# # ====================== 通用导入函数（支持列名映射） ======================
# def import_std_by_columns(file_path, std_code, std_name, col_map):
#     """
#     col_map: dict 例如 {'category':'核心内容分类', 'item':'具体项目', 'content':'详细要求'}
#     """
#     print(f"正在导入 {std_code} ...")
#     df = pd.read_excel(file_path, engine='openpyxl')
#     # 检查必要列是否存在
#     need_cols = [col_map['category'], col_map['item'], col_map['content']]
#     if not all(col in df.columns for col in need_cols):
#         raise ValueError(f"文件 {file_path} 缺少必要列 {need_cols}，实际列名：{list(df.columns)}")
    
#     doc_id = insert_standard_document(std_code, std_name)

#     for idx, row in df.iterrows():
#         category = row[col_map['category']]
#         item = row[col_map['item']]
#         content = row[col_map['content']]
        
#         if pd.isna(category) and pd.isna(item) and pd.isna(content):
#             continue
        
#         cat_str = str(category) if pd.notna(category) else ''
#         item_str = str(item) if pd.notna(item) else ''
#         content_str = str(content) if pd.notna(content) else ''
        
#         # 判断是否为术语
#         if '术语和定义' in cat_str:
#             term_name = parse_term_name(item_str)
#             if term_name and content_str and content_str != 'nan':
#                 term_cat = get_term_category(cat_str)
#                 with engine.connect() as conn:
#                     conn.execute(
#                         text("INSERT INTO term (name, definition, category, source_standard, standard_doc_id) VALUES (:name, :def, :cat, :src, :doc_id)"),
#                         {"name": term_name, "def": content_str, "cat": term_cat, "src": std_code, "doc_id": doc_id}
#                     )
#                     conn.commit()
#         else:
#             # 非术语条目，存入 standard_content
#             if cat_str and cat_str != 'nan' and item_str and item_str != 'nan':
#                 insert_standard_content(doc_id, cat_str, item_str, content_str)

# # ====================== 主程序 ======================
# if __name__ == '__main__':
#     # 定义文件列表及对应的文档信息、列映射
#     files = [
#         (r'C:\Users\86158\Desktop\T CIATCM 100-2023.xlsx', 'T/CIATCM 100-2023', '中医药文本挖掘数据集构建规范',
#          {'category': '核心内容分类', 'item': '具体项目', 'content': '详细要求'}),
#         (r'C:\Users\86158\Desktop\T CIATCM 102-2023.xlsx', 'T/CIATCM 102-2023', '中医药信息基本数据集编制规则',
#          {'category': '核心内容分类', 'item': '具体项目', 'content': '详细要求'}),
#         (r'C:\Users\86158\Desktop\T CIATCM 109-2024.xlsx', 'T/CIATCM 109-2024', '张仲景经方传承信息数据元值域代码',
#          {'category': '模块类型', 'item': '核心内容', 'content': '详细说明'}),
#         (r'C:\Users\86158\Desktop\T CIATCM 110-2024.xlsx', 'T/CIATCM 110-2024', '中医四诊合参病案数据采集规范',
#          {'category': '核心内容分类', 'item': '具体项目', 'content': '详细要求'})
#     ]
    
#     for file_path, code, name, col_map in files:
#         import_std_by_columns(file_path, code, name, col_map)
    
#     print("所有数据导入完成！")



# import pandas as pd
# from sqlalchemy import create_engine, text
# import re

# # ====================== 数据库连接配置 ======================
# DB_USER = 'root'
# DB_PASSWORD = '0421'   # 改成你的密码
# DB_HOST = 'localhost'
# DB_NAME = 'standard_db'   # 数据库名

# engine = create_engine(f'mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}?charset=utf8mb4')

# # ====================== 辅助函数 ======================
# def insert_standard_document(std_code, std_name):
#     """插入或获取标准文档ID"""
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

# def insert_standard_content(doc_id, category, item_name, content):
#     with engine.connect() as conn:
#         conn.execute(
#             text("INSERT INTO standard_content (standard_doc_id, category, item_name, content) VALUES (:doc_id, :cat, :item, :cont)"),
#             {"doc_id": doc_id, "cat": category, "item": item_name, "cont": content}
#         )
#         conn.commit()

# def parse_term_name(item):
#     """从带编号的术语项中提取纯名称，如 '3.1 张仲景经方（...）' → '张仲景经方'"""
#     if pd.isna(item):
#         return ''
#     s = str(item)
#     match = re.search(r'\d+(?:\.\d+)*\s+([^（(]+)', s)
#     if match:
#         return match.group(1).strip()
#     if ' ' in s:
#         return s.split()[0]
#     return s

# def get_term_category(category_str):
#     """根据分类字符串确定术语的所属大类"""
#     if pd.isna(category_str):
#         return ''
#     if '基础设施' in category_str:
#         return '基础设施'
#     if '数据资源' in category_str:
#         return '数据资源'
#     if '应用系统' in category_str:
#         return '应用系统'
#     if '支撑体系' in category_str:
#         return '支撑体系'
#     if '术语和定义' in category_str:
#         return '基础'
#     return '其他'

# # ====================== 通用导入函数（支持列名映射） ======================
# def import_std_by_columns(file_path, std_code, std_name, col_map):
#     """
#     col_map: dict 例如 {'category':'核心内容分类', 'item':'具体项目', 'content':'详细要求'}
#     """
#     print(f"正在导入 {std_code} ...")
#     df = pd.read_excel(file_path, engine='openpyxl')
#     need_cols = [col_map['category'], col_map['item'], col_map['content']]
#     if not all(col in df.columns for col in need_cols):
#         raise ValueError(f"文件 {file_path} 缺少必要列 {need_cols}，实际列名：{list(df.columns)}")
    
#     doc_id = insert_standard_document(std_code, std_name)

#     for idx, row in df.iterrows():
#         category = row[col_map['category']]
#         item = row[col_map['item']]
#         content = row[col_map['content']]
        
#         if pd.isna(category) and pd.isna(item) and pd.isna(content):
#             continue
        
#         cat_str = str(category) if pd.notna(category) else ''
#         item_str = str(item) if pd.notna(item) else ''
#         content_str = str(content) if pd.notna(content) else ''
        
#         # 判断是否为术语（分类中包含“术语和定义”）
#         if '术语和定义' in cat_str:
#             term_name = parse_term_name(item_str)
#             if term_name and content_str and content_str != 'nan':
#                 term_cat = get_term_category(cat_str)
#                 with engine.connect() as conn:
#                     conn.execute(
#                         text("INSERT INTO term (name, definition, category, source_standard, standard_doc_id) VALUES (:name, :def, :cat, :src, :doc_id)"),
#                         {"name": term_name, "def": content_str, "cat": term_cat, "src": std_code, "doc_id": doc_id}
#                     )
#                     conn.commit()
#         else:
#             # 非术语条目，存入 standard_content
#             if cat_str and cat_str != 'nan' and item_str and item_str != 'nan':
#                 insert_standard_content(doc_id, cat_str, item_str, content_str)

# # ====================== 主程序 ======================
# if __name__ == '__main__':
#     # 定义文件列表及对应的文档信息、列映射
#     files = [
#         # 038 文件使用核心内容分类 / 具体项目 / 详细要求
#         (r'C:\Users\86158\Desktop\T CIATCM 038-2019.xlsx', 'T/CIATCM 038-2019', '中医医院科研管理信息系统基本功能规范',
#          {'category': '核心内容分类', 'item': '具体项目', 'content': '详细要求'}),
#         # 094 文件使用核心分类 / 具体条目 / 详细内容
#         (r'C:\Users\86158\Desktop\T CIATCM 094-2020.xlsx', 'T/CIATCM 094-2020', '基层医疗卫生机构中医诊疗区(中医馆)数据接口技术规范',
#          {'category': '核心分类', 'item': '具体条目', 'content': '详细内容'}),
#         # 097 文件使用核心内容分类 / 具体项目 / 详细要求
#         (r'C:\Users\86158\Desktop\T CIATCM 097-2023.xlsx', 'T/CIATCM 097-2023', '中医药科学数据汇交信息基本数据集',
#          {'category': '核心内容分类', 'item': '具体项目', 'content': '详细要求'}),
#         # 105 文件使用核心分类 / 具体条目 / 详细内容
#         (r'C:\Users\86158\Desktop\T CIATCM 105-2023.xlsx', 'T/CIATCM 105-2023', '藏药资源信息数据元目录',
#          {'category': '核心分类', 'item': '具体条目', 'content': '详细内容'}),
#     ]
    
#     for file_path, code, name, col_map in files:
#         import_std_by_columns(file_path, code, name, col_map)
    
#     print("所有数据导入完成！")




import pandas as pd
from sqlalchemy import create_engine, text
import re

# ====================== 数据库连接配置 ======================
DB_USER = 'root'
DB_PASSWORD = '0421'   # 改成你的密码
DB_HOST = 'localhost'
DB_NAME = 'standard_db'   # 数据库名

engine = create_engine(f'mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}?charset=utf8mb4')

# ====================== 辅助函数 ======================
def insert_standard_document(std_code, std_name):
    """插入或获取标准文档ID"""
    with engine.connect() as conn:
        result = conn.execute(
            text("SELECT id FROM standard_document WHERE code = :code"),
            {"code": std_code}
        ).fetchone()
        if result:
            return result[0]
        else:
            conn.execute(
                text("INSERT INTO standard_document (code, name) VALUES (:code, :name)"),
                {"code": std_code, "name": std_name}
            )
            conn.commit()
            return conn.execute(
                text("SELECT id FROM standard_document WHERE code = :code"),
                {"code": std_code}
            ).fetchone()[0]

def insert_standard_content(doc_id, category, item_name, content):
    with engine.connect() as conn:
        conn.execute(
            text("INSERT INTO standard_content (standard_doc_id, category, item_name, content) VALUES (:doc_id, :cat, :item, :cont)"),
            {"doc_id": doc_id, "cat": category, "item": item_name, "cont": content}
        )
        conn.commit()

def parse_term_name(item):
    """从带编号的术语项中提取纯名称，如 '3.1 张仲景经方（...）' → '张仲景经方'"""
    if pd.isna(item):
        return ''
    s = str(item)
    match = re.search(r'\d+(?:\.\d+)*\s+([^（(]+)', s)
    if match:
        return match.group(1).strip()
    if ' ' in s:
        return s.split()[0]
    return s

def get_term_category(category_str):
    """根据分类字符串确定术语的所属大类"""
    if pd.isna(category_str):
        return ''
    if '基础设施' in category_str:
        return '基础设施'
    if '数据资源' in category_str:
        return '数据资源'
    if '应用系统' in category_str:
        return '应用系统'
    if '支撑体系' in category_str:
        return '支撑体系'
    if '术语和定义' in category_str:
        return '基础'
    return '其他'

# ====================== 通用导入函数（支持列名映射） ======================
def import_std_by_columns(file_path, std_code, std_name, col_map):
    """
    col_map: dict 例如 {'category':'核心内容分类', 'item':'具体项目', 'content':'详细要求'}
    """
    print(f"正在导入 {std_code} ...")
    df = pd.read_excel(file_path, engine='openpyxl')
    need_cols = [col_map['category'], col_map['item'], col_map['content']]
    if not all(col in df.columns for col in need_cols):
        raise ValueError(f"文件 {file_path} 缺少必要列 {need_cols}，实际列名：{list(df.columns)}")
    
    doc_id = insert_standard_document(std_code, std_name)

    for idx, row in df.iterrows():
        category = row[col_map['category']]
        item = row[col_map['item']]
        content = row[col_map['content']]
        
        if pd.isna(category) and pd.isna(item) and pd.isna(content):
            continue
        
        cat_str = str(category) if pd.notna(category) else ''
        item_str = str(item) if pd.notna(item) else ''
        content_str = str(content) if pd.notna(content) else ''
        
        # 判断是否为术语（分类中包含“术语和定义”）
        if '术语和定义' in cat_str:
            term_name = parse_term_name(item_str)
            if term_name and content_str and content_str != 'nan':
                term_cat = get_term_category(cat_str)
                with engine.connect() as conn:
                    conn.execute(
                        text("INSERT INTO term (name, definition, category, source_standard, standard_doc_id) VALUES (:name, :def, :cat, :src, :doc_id)"),
                        {"name": term_name, "def": content_str, "cat": term_cat, "src": std_code, "doc_id": doc_id}
                    )
                    conn.commit()
        else:
            # 非术语条目，存入 standard_content
            if cat_str and cat_str != 'nan' and item_str and item_str != 'nan':
                insert_standard_content(doc_id, cat_str, item_str, content_str)

# ====================== 主程序 ======================
if __name__ == '__main__':
    # 定义文件列表及对应的文档信息、列映射
    files = [
        # 037 文件使用核心内容分类 / 具体项目 / 详细要求
        (r'C:\Users\86158\Desktop\T CIATCM 037-2019.xlsx', 'T/CIATCM 037-2019', '中医医疗信息标准特征性描述框架',
         {'category': '核心内容分类', 'item': '具体项目', 'content': '详细要求'}),
        # 007 文件使用核心内容分类 / 具体项目 / 详细要求
        (r'C:\Users\86158\Desktop\T CIATCM 007-2019.xlsx', 'T/CIATCM 007-2019', '中医药综合统计网络直报信息系统基本功能规范',
         {'category': '核心内容分类', 'item': '具体项目', 'content': '详细要求'}),
        # 104 文件使用核心分类 / 具体条目 / 详细内容
        (r'C:\Users\86158\Desktop\T CIATCM 104-2023.xlsx', 'T/CIATCM 104-2023', '张仲景经方传承信息基本数据集',
         {'category': '核心分类', 'item': '具体条目', 'content': '详细内容'}),
    ]
    
    for file_path, code, name, col_map in files:
        import_std_by_columns(file_path, code, name, col_map)
    
    print("所有数据导入完成！")