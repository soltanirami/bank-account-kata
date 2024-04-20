Creating an arbitrage bot involves several steps including accessing market data, making trade decisions, and executing trades. Here's a simplified Python code outline for an arbitrage bot that trades ETH between two exchanges, assuming you have access to their APIs:

```python
import requests
import json
from web3 import Web3

# Define your Ethereum wallet address
wallet_address = "Your_Wallet_Address"

# Define the API endpoints for the exchanges
exchange1_api_url = "Exchange1_API_URL"
exchange2_api_url = "Exchange2_API_URL"

# Connect to Ethereum node
web3 = Web3(Web3.HTTPProvider('https://mainnet.infura.io/v3/your_infura_project_id'))

# Define your private key
private_key = "Your_Private_Key"

# Function to fetch ETH balance
def get_eth_balance():
    return web3.eth.get_balance(wallet_address)

# Function to fetch ETH price from exchange1
def get_eth_price_exchange1():
    response = requests.get(exchange1_api_url)
    data = response.json()
    return data['eth_price']

# Function to fetch ETH price from exchange2
def get_eth_price_exchange2():
    response = requests.get(exchange2_api_url)
    data = response.json()
    return data['eth_price']

# Function to execute ETH transfer
def execute_eth_transfer(destination_address, amount):
    # Build transaction
    transaction = {
        'to': destination_address,
        'value': amount,
        'gas': 21000,
        'gasPrice': web3.eth.gas_price,
        'nonce': web3.eth.get_transaction_count(wallet_address)
    }
    # Sign transaction
    signed_txn = web3.eth.account.sign_transaction(transaction, private_key)
    # Send transaction
    tx_hash = web3.eth.send_raw_transaction(signed_txn.rawTransaction)
    return tx_hash

# Main function for arbitrage bot
def arbitrage_bot():
    # Get ETH balance
    eth_balance = get_eth_balance()
    
    # Get ETH prices from both exchanges
    price_exchange1 = get_eth_price_exchange1()
    price_exchange2 = get_eth_price_exchange2()
    
    # Calculate potential profit
    profit = (eth_balance * price_exchange2) - (eth_balance * price_exchange1)
    
    if profit > 0:
        # Arbitrage opportunity found, execute transfer
        destination_address = "Destination_Address"
        amount_to_transfer = eth_balance
        tx_hash = execute_eth_transfer(destination_address, amount_to_transfer)
        print("Arbitrage opportunity exploited! Transaction hash:", tx_hash.hex())
    else:
        print("No arbitrage opportunity found.")

# Run the arbitrage bot
arbitrage_bot()
```

Replace `"Your_Wallet_Address"`, `"Your_Private_Key"`, `"Exchange1_API_URL"`, `"Exchange2_API_URL"`, and `"Destination_Address"` with your actual wallet address, private key, API endpoints for the exchanges, and the address where you want to transfer the ETH when an arbitrage opportunity is found.

Note: This code is a basic outline and might need adjustments based on the specific APIs of the exchanges you are using and other factors such as fees, slippage, and error handling. Additionally, make sure to thoroughly test the bot in a safe environment before using it with real funds.