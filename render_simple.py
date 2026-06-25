import re
import os
import base64
import urllib.request

def encode_mermaid(mermaid_code):
    encoded = base64.urlsafe_b64encode(mermaid_code.encode('utf-8')).decode('utf-8')
    encoded = encoded.rstrip('=')
    return encoded

def render_kroki_url(mermaid_code, output_path):
    encoded = encode_mermaid(mermaid_code)
    url = f"https://kroki.io/mermaid/png/{encoded}"
    
    try:
        req = urllib.request.Request(url, headers={
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        })
        with urllib.request.urlopen(req, timeout=60) as response:
            if response.status == 200:
                with open(output_path, 'wb') as f:
                    f.write(response.read())
                print(f"[OK] Rendered -> {output_path}")
                return True
            else:
                print(f"[FAIL] HTTP {response.status}")
                return False
    except Exception as e:
        print(f"[ERROR] {e}")
        return False

def extract_mermaid_blocks(md_path):
    with open(md_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    pattern = r'```mermaid\s*\n(.*?)```'
    matches = re.findall(pattern, content, re.DOTALL)
    return matches

def main():
    md_file = 'simple_architecture.md'
    output_dir = 'diagrams'
    
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    mermaid_blocks = extract_mermaid_blocks(md_file)
    
    if mermaid_blocks:
        output_path = os.path.join(output_dir, 'simple_architecture.png')
        print("Rendering simple architecture diagram...")
        render_kroki_url(mermaid_blocks[0], output_path)

if __name__ == '__main__':
    main()
