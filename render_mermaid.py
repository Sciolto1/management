import re
import os
import base64
import json
import urllib.request
import urllib.error

def encode_mermaid_kroki(mermaid_code):
    encoded = base64.urlsafe_b64encode(mermaid_code.encode('utf-8')).decode('utf-8')
    encoded = encoded.rstrip('=')
    return encoded

def render_kroki(mermaid_code, output_path, index, diagram_type='mermaid'):
    payload = {
        'diagram_source': mermaid_code,
        'diagram_type': diagram_type,
        'output_format': 'png'
    }
    
    data = json.dumps(payload).encode('utf-8')
    url = "https://kroki.io/"
    
    try:
        req = urllib.request.Request(url, data=data, headers={'Content-Type': 'application/json'})
        with urllib.request.urlopen(req, timeout=60) as response:
            if response.status == 200:
                with open(output_path, 'wb') as f:
                    f.write(response.read())
                print(f"  [OK] Chart {index} rendered -> {output_path}")
                return True
            else:
                print(f"  [FAIL] Chart {index}: HTTP {response.status}")
                return False
    except urllib.error.URLError as e:
        print(f"  [ERROR] Chart {index}: {e}")
        return False
    except Exception as e:
        print(f"  [ERROR] Chart {index}: {e}")
        return False

def render_mermaid_ink(mermaid_code, output_path, index):
    encoded = encode_mermaid_kroki(mermaid_code)
    url = f"https://mermaid.ink/img/{encoded}"
    
    try:
        req = urllib.request.Request(url, headers={
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        })
        with urllib.request.urlopen(req, timeout=30) as response:
            if response.status == 200:
                with open(output_path, 'wb') as f:
                    f.write(response.read())
                print(f"  [OK] Chart {index} rendered -> {output_path}")
                return True
            else:
                print(f"  [FAIL] Chart {index}: HTTP {response.status}")
                return False
    except urllib.error.URLError as e:
        print(f"  [ERROR] Chart {index}: {e}")
        return False
    except Exception as e:
        print(f"  [ERROR] Chart {index}: {e}")
        return False

def extract_mermaid_blocks(md_path):
    with open(md_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    pattern = r'```mermaid\s*\n(.*?)```'
    matches = re.findall(pattern, content, re.DOTALL)
    
    print(f"Found {len(matches)} mermaid blocks")
    return matches

def main():
    md_file = '技术解决方案.md'
    output_dir = 'diagrams'
    
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    mermaid_blocks = extract_mermaid_blocks(md_file)
    
    chart_names = [
        '系统架构图简约版',
        '系统架构图详细版',
        '用户认证模块类图',
        '员工管理模块类图',
        '部门管理模块类图',
        '考勤管理模块类图',
        '请假管理模块类图',
        '工资管理模块类图',
        '绩效考核模块类图',
        '培训管理模块类图',
        '公积金管理模块类图',
        '通知公告模块类图'
    ]
    
    success_count = 0
    for i, code in enumerate(mermaid_blocks):
        if i < len(chart_names):
            name = chart_names[i]
        else:
            name = f'chart_{i+1}'
        
        safe_name = name.replace('/', '_').replace('\\', '_')
        output_path = os.path.join(output_dir, f'chart_{i+1:02d}_{safe_name}.png')
        
        print(f"\nRendering chart {i+1}: {name}")
        
        success = render_kroki(code, output_path, i+1)
        if not success:
            print("  Trying mermaid.ink...")
            success = render_mermaid_ink(code, output_path, i+1)
        
        if success:
            success_count += 1
    
    print(f"\n{'='*50}")
    print(f"Done! {success_count}/{len(mermaid_blocks)} charts rendered successfully.")
    print(f"Output directory: {output_dir}")

if __name__ == '__main__':
    main()
