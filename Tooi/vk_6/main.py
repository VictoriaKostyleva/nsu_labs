import vk
from time import time, sleep
from vk.exceptions import VkAPIError

session = vk.Session(access_token='1299312e06bc2a03a977634dbf68a42350772b563565f1b6699c902adf6173ad59e7319a8e25a10d355ca')

class MyAPI(vk.API):
	def __init__(self, *args, **kwargs):
		super().__init__(*args, **kwargs)
		self.last_query = 0

	def _get_fr(self, **kwargs):
		while (time() - self.last_query < 1/3):
			sleep(1/30)

		
		while True:
			try:
				self.last_query = time()
				return self.friends.get(**kwargs)
			except VkAPIError as vke:
				print("https://vk.com/dev/errors: {}!".format(vke.code))

				if vke.code == 6:
					sleep(0.5)
					continue
				elif vke.code == 18:
					return {"count": 0, "items": []}

				raise vke

	def get_friends(self, user_id):
		friends = []

		response = self._get_fr(user_id=user_id, count=5000, offset=0)

		friends_all = response['count']
		friends += response['items']

		for i in range(1, friends_all // 5000 + 1):
			response = self._get_fr(user_id=user_id, count=5000, offset=i*5000)
			friends += response['items']

		return friends

api = MyAPI(session, v="5.8", lang="ru", timeout=10)


class Friends:
	def __init__(self, _id, _max=None):
		self.id = _id

		self.ids = {}

		self._max = _max #for getting some friends(not all)

		self._get_friends()

	def _get_friends(self): 
		first = api.get_friends(self.id)
		self.ids[self.id] = first

		for i, fid in enumerate(first):
			if self._max and i > self._max:
				break
			print("Get {}/{}".format(i, len(first)))
			
			self.ids[fid] = api.get_friends(fid)	

	def get_chain(self, _id):
		chain = [_id]
		while _id != self.id:
			_id = self[_id] # self.__getitem__[_id]
			chain.append(_id)
		return chain[::-1]

	def __getitem__(self, value):
		if value in self.ids[self.id]:
			return self.id

		for k, v in self.ids.items():
			if value in v:
				return k

		raise KeyError(key)

	def __contains__(self, value):
		for v in self.ids.values():
			if value in v:
				return True
		return None


A_id = 138759322
B_id = 53083705

friends = Friends(A_id)


_pairs = []
def search_deep(_id, deep, chain=None):
	chain = chain or []

	chain += [_id]

	if _id in friends:
		return chain

	if deep > 0:
		fr = api.get_friends(_id)
		for fid in fr:
			if (fid, _id) in _pairs:
				continue

			if fid in friends:
				return chain + [fid]

			_pairs.append((fid, _id))

			res = search_deep(fid, deep - 1, chain + [fid])
			if res:
				return res

	return None

chain = search_deep(B_id, 2)
if chain is None:
	print("Not found :(")
	exit()

chain = chain[::-1]
print(chain)

# print in friends


chain = friends.get_chain(chain[0]) + chain[1:]

print(chain)
sleep(1)
for info in api.users.get(user_ids=chain):
	print(info)



















